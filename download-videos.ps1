# Simple video downloader for SpyBrain backgrounds
Write-Host "Downloading video backgrounds..." -ForegroundColor Green

# Create directory
$outputDir = "app\src\main\res\raw"
if (!(Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force
}

# Download sample videos (these are placeholder URLs)
$videos = @(
    @{ Name = "video_water_loop.mp4"; Url = "https://file-examples.com/storage/febc86d2b4c0c72cc85db55/2017/10/file_example_MP4_480_1_5MG.mp4" },
    @{ Name = "video_space_loop.mp4"; Url = "https://file-examples.com/storage/febc86d2b4c0c72cc85db55/2017/10/file_example_MP4_640_3MG.mp4" },
    @{ Name = "video_nature_loop.mp4"; Url = "https://file-examples.com/storage/febc86d2b4c0c72cc85db55/2017/10/file_example_MP4_1280_10MG.mp4" }
)

foreach ($video in $videos) {
    $outputPath = Join-Path $outputDir $video.Name
    Write-Host "Downloading $($video.Name)..."
    
    try {
        Invoke-WebRequest -Uri $video.Url -OutFile $outputPath -UseBasicParsing
        Write-Host "Downloaded: $($video.Name)" -ForegroundColor Green
    }
    catch {
        Write-Host "Failed to download $($video.Name)" -ForegroundColor Red
    }
}

Write-Host "Done! Check folder: $outputDir" -ForegroundColor Green
Write-Host ""
Write-Host "Manual download recommended from:"
Write-Host "- https://mixkit.co/free-stock-video/"
Write-Host "- https://coverr.co/"
Write-Host "- https://videvo.net/free-video/"


