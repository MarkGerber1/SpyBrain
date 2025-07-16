Get-ChildItem -Path app/src/main/kotlin -Recurse -Filter *.kt | ForEach-Object {
    $file = $_.FullName
    (Get-Content $file) | ForEach-Object { $_.TrimEnd() } | Set-Content $file -Encoding UTF8
    Write-Host "Processed file: $file"
}
Write-Host "Done: all trailing whitespaces removed." 