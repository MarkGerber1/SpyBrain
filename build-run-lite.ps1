param(
  [switch]$ShowGitDiff = $false,
  [int]$StallSeconds = 180
)

$ErrorActionPreference = 'Stop'
$ProgressPreference    = 'SilentlyContinue'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "==> build-run-lite.ps1 v4.2"

function Start-NativeLogged {
  param(
    [Parameter(Mandatory=$true)][string]$CommandLine,
    [Parameter(Mandatory=$true)][string]$LogPath
  )
  if (Test-Path $LogPath) { Remove-Item $LogPath -Force }
  # Важно: stderr->stdout внутри cmd (2>&1). Редиректим только stdout в файл.
  $cmdWithRedirect = "$CommandLine 2>&1"
  $p = Start-Process -FilePath "cmd.exe" `
        -ArgumentList @("/c", $cmdWithRedirect) `
        -NoNewWindow -PassThru `
        -RedirectStandardOutput $LogPath
  return $p
}

function Wait-WithTail {
  param(
    [Parameter(Mandatory=$true)][System.Diagnostics.Process]$Process,
    [Parameter(Mandatory=$true)][string]$LogPath,
    [int]$StallSeconds = 180
  )
  # Стримим лог в консоль, пока процесс работает
  $tailJob = Start-Job -ScriptBlock { param($p) Get-Content -Path $p -Wait } -ArgumentList $LogPath
  $lastLen = -1
  $stall = 0
  while (-not $Process.HasExited) {
    Start-Sleep -Seconds 2
    if (Test-Path $LogPath) {
      $len = (Get-Item $LogPath).Length
      if ($len -gt $lastLen) { $lastLen = $len; $stall = 0 } else { $stall += 2 }
    } else {
      $stall += 2
    }
    if ($stall -ge $StallSeconds) {
      Write-Warning "No output for $StallSeconds sec. Killing process tree..."
      try {
        Stop-Process -Id $Process.Id -Force -ErrorAction SilentlyContinue
        & taskkill /F /T /PID $Process.Id | Out-Null
      } catch {}
      break
    }
  }
  # Подчистим tail-джоб и покажем хвост
  Wait-Job $tailJob -Any -Timeout 1 | Out-Null
  Stop-Job $tailJob -ErrorAction SilentlyContinue | Out-Null
  Receive-Job $tailJob -ErrorAction SilentlyContinue | Out-Null
  if (Test-Path $LogPath) {
    Write-Host "`n==> tail of log:"
    Get-Content $LogPath -Tail 80
  }
  return $Process.ExitCode
}

# --- Проверки окружения ---
if (-not (Test-Path .\gradlew.bat)) {
  Write-Error "gradlew.bat not found. Run the script from the project root."
}

$adbAvailable = $false
try { $null = Get-Command adb -ErrorAction Stop; $adbAvailable = $true } catch {
  Write-Warning "ADB not found in PATH. Will install APK via Gradle only (no auto-run)."
}

# --- Остановить демоны Gradle ---
& .\gradlew.bat --stop | Out-Null

# --- Сборка ---
$env:CI = 'true'
$buildLog = Join-Path $PWD "build.log"
$buildCmd = ".\gradlew.bat clean assembleDebug --no-daemon --console=plain --stacktrace --warning-mode=all --info --no-configuration-cache"
Write-Host "==> Building: $buildCmd"
$p = Start-NativeLogged -CommandLine $buildCmd -LogPath $buildLog
$code = Wait-WithTail -Process $p -LogPath $buildLog -StallSeconds $StallSeconds
if ($code -ne 0) {
  Write-Host "BUILD FAILED (exit $code). See $buildLog"
  exit $code
}
Write-Host "BUILD OK. Log: $buildLog"

# --- Установка APK ---
$instLog = Join-Path $PWD "install.log"
$instCmd = ".\gradlew.bat :app:installDebug --no-daemon --console=plain --info"
Write-Host "==> Installing: $instCmd"
$p = Start-NativeLogged -CommandLine $instCmd -LogPath $instLog
$code = Wait-WithTail -Process $p -LogPath $instLog -StallSeconds $StallSeconds
if ($code -ne 0) {
  Write-Host "INSTALL FAILED (exit $code). See $instLog"
  exit $code
}
Write-Host "INSTALL OK"

# --- Определить applicationId ---
$appId = $null
$meta = "app/build/outputs/apk/debug/output-metadata.json"
if (Test-Path $meta) {
  try {
    $json = Get-Content $meta -Raw | ConvertFrom-Json
    if ($json.elements -and $json.elements.Count -gt 0) { $appId = $json.elements[0].applicationId }
  } catch {}
}
if (-not $appId) { $appId = "com.example.spybrain.debug" }
$altAppId = "com.example.spybrain"

function Guess-Package {
  param([string]$needle)
  try {
    $packages = adb shell pm list packages 2>$null
    if ($LASTEXITCODE -eq 0 -and $packages) {
      $found = ($packages -split "`n" | Where-Object { $_ -match $needle }) | Select-Object -First 1
      if ($found) { return ($found.Trim() -replace '^package:','') }
    }
  } catch {}
  return $null
}

if ($adbAvailable -and $appId -eq "com.example.spybrain.debug") {
  $guess = Guess-Package "spybrain"
  if ($guess) { $appId = $guess }
}

# --- Запуск и логкат ---
$started = $false
if ($adbAvailable) {
  Write-Host "==> Launching app ..."
  foreach ($pkg in @($appId, $altAppId)) {
    $res = adb shell am start -n "$pkg/.MainActivity" 2>&1
    if ($LASTEXITCODE -eq 0 -and ($res -match "Starting: Intent")) {
      Write-Host "Launched: $pkg/.MainActivity"
      $started = $true
      $appId = $pkg
      break
    }
  }
  if (-not $started) {
    Write-Host "Fallback: monkey launcher ..."
    adb shell monkey -p $appId -c android.intent.category.LAUNCHER 1 | Out-Null
    if ($LASTEXITCODE -eq 0) { Write-Host "Launched via monkey: $appId"; $started = $true }
    else { Write-Warning "Could not auto-launch the app." }
  }

  if ($started) {
    Write-Host "==> Capturing logcat by PID ..."
    $ts = Get-Date -Format "yyyyMMdd-HHmmss"
    $logsDir = Join-Path $PWD "logs"
    if (-not (Test-Path $logsDir)) { New-Item -ItemType Directory -Path $logsDir | Out-Null }

    adb logcat -c | Out-Null
    Start-Sleep -Milliseconds 300

    $pid = (adb shell pidof -s $appId).Trim()
    if (-not $pid) {
      $psOut = adb shell ps 2>$null
      if ($psOut) {
        $line = ($psOut -split "`n" | Where-Object { $_ -match $appId }) | Select-Object -First 1
        if ($line) { $parts = $line -split "\s+"; if ($parts.Length -gt 1) { $pid = $parts[1] } }
      }
    }

    $namePart = $(if ($pid) { $appId } else { "generic" })
    $tsLog = Join-Path $logsDir ("run-{0}-{1}.txt" -f $ts, $namePart)

    if ($pid) {
      adb logcat --pid=$pid -d -v time | Tee-Object -FilePath $tsLog | Out-Null
    } else {
      Write-Warning "PID not found. Saving generic log tail ..."
      adb logcat -d -v time | Tee-Object -FilePath $tsLog | Out-Null
    }
    Write-Host "Saved log: $tsLog"
  }
}

# --- Git diff ---
if ($ShowGitDiff) {
  Write-Host "==> Git diff summary ..."
  git --no-pager status -s
  git --no-pager diff --stat -- . ':(exclude)*.mp3' ':(exclude)*.wav' ':(exclude)app/src/main/res/raw' ':(exclude)app/src/main/res/drawable*'
}

Write-Host "Done."


