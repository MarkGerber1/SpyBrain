# Скрипт для скачивания видео-фонов
param(
    [string]$OutputDir = "app\src\main\res\raw"
)

Write-Host "🎬 Скачивание видео-фонов для SpyBrain..." -ForegroundColor Green

# Создаем папку если не существует
if (!(Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
    Write-Host "📁 Создана папка: $OutputDir" -ForegroundColor Yellow
}

# Массив URL для скачивания (примеры бесплатных видео)
$videos = @(
    @{
        Name = "video_water_loop.mp4"
        Url = "https://sample-videos.com/zip/10/mp4/240/SampleVideo_240x426_30mb.mp4"
        Theme = "WATER"
        Description = "Океанские волны"
    },
    @{
        Name = "video_space_loop.mp4" 
        Url = "https://sample-videos.com/zip/10/mp4/240/SampleVideo_240x426_10mb.mp4"
        Theme = "SPACE"
        Description = "Космические частицы"
    },
    @{
        Name = "video_nature_loop.mp4"
        Url = "https://sample-videos.com/zip/10/mp4/240/SampleVideo_240x426_5mb.mp4"
        Theme = "NATURE"
        Description = "Природная анимация"
    }
)

# Альтернативные источники
$alternatives = @(
    "🌊 WATER тематика:",
    "   - https://coverr.co/videos/ocean-waves",
    "   - https://mixkit.co/free-stock-video/ocean/",
    "   - Поиск: 'ocean waves loop seamless'",
    "",
    "🌌 SPACE тематика:",
    "   - https://coverr.co/videos/space", 
    "   - https://mixkit.co/free-stock-video/space/",
    "   - Поиск: 'space stars nebula loop'",
    "",
    "🌿 NATURE тематика:",
    "   - https://coverr.co/videos/nature",
    "   - https://mixkit.co/free-stock-video/nature/",
    "   - Поиск: 'forest leaves wind loop'"
)

Write-Host "`n📋 Рекомендуемые источники для ручного скачивания:" -ForegroundColor Cyan
foreach ($alt in $alternatives) {
    Write-Host $alt -ForegroundColor Gray
}

Write-Host "`n🔄 Пробую автоматическое скачивание тестовых файлов..." -ForegroundColor Yellow

foreach ($video in $videos) {
    $outputPath = Join-Path $OutputDir $video.Name
    
    Write-Host "⬇️  Скачиваю $($video.Theme): $($video.Description)" -ForegroundColor Blue
    
    try {
        # Используем Invoke-WebRequest для скачивания
        Invoke-WebRequest -Uri $video.Url -OutFile $outputPath -UseBasicParsing -TimeoutSec 30
        
        if (Test-Path $outputPath) {
            $fileSize = (Get-Item $outputPath).Length / 1MB
            Write-Host "   ✅ Скачано: $($video.Name) ($('{0:N1}' -f $fileSize) MB)" -ForegroundColor Green
        }
    }
    catch {
        Write-Host "   ❌ Ошибка скачивания $($video.Name): $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "   💡 Скачайте вручную с рекомендуемых сайтов выше" -ForegroundColor Yellow
    }
}

Write-Host "`n📋 Инструкции для ручного скачивания:" -ForegroundColor Cyan
Write-Host "1. Перейдите на https://mixkit.co/free-stock-video/" -ForegroundColor White
Write-Host "2. Найдите короткие (3-10 сек) зацикленные видео" -ForegroundColor White  
Write-Host "3. Скачайте и переименуйте:" -ForegroundColor White
Write-Host "   - video_water_loop.mp4 (океан, вода, волны)" -ForegroundColor Cyan
Write-Host "   - video_space_loop.mp4 (космос, звезды, галактики)" -ForegroundColor Cyan
Write-Host "   - video_nature_loop.mp4 (лес, природа, листья)" -ForegroundColor Cyan
Write-Host "4. Поместите файлы в папку: $OutputDir" -ForegroundColor White

Write-Host "`n🛠️  После добавления видео выполните:" -ForegroundColor Green
Write-Host "   .\gradlew assembleDebug && adb install -r app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Gray

Write-Host "`n🎯 Готово! Проверьте папку $OutputDir" -ForegroundColor Green


