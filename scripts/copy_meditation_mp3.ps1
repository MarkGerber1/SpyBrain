param(
    [Parameter(Mandatory=$true)] [string]$SourceDir
)

$ErrorActionPreference = 'Stop'

Write-Host "SourceDir: $SourceDir"

$dst = "app\src\main\res\raw"
if (!(Test-Path -LiteralPath $dst)) {
    New-Item -ItemType Directory -Path $dst | Out-Null
}

$files = Get-ChildItem -LiteralPath $SourceDir -Filter *.mp3 | Sort-Object Name
if (!$files -or $files.Count -eq 0) {
    throw "No mp3 files found in '$SourceDir'"
}

$names = @(
    'meditation_angelic',
    'meditation_chill',
    'meditation_dreaming',
    'meditation_forest_spirit',
    'meditation_night_sky',
    'meditation_relaxation',
    'meditation_spiritual',
    'meditation_valley_sunset'
)

$idx = 0
foreach ($name in $names) {
    $f = $files[$idx % $files.Count]
    $target = Join-Path $dst ("$name.mp3")
    Copy-Item -LiteralPath $f.FullName -Destination $target -Force
    Write-Host "Copied '$($f.Name)' -> '$target'"
    $idx++
}

Write-Host "Done: copied $($names.Count) files"


