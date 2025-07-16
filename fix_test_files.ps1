Get-ChildItem -Path app/src/test/kotlin -Recurse -Filter *.kt | ForEach-Object {
    $file = $_.FullName
    # Удаляем пробелы в конце строк
    $lines = Get-Content $file | ForEach-Object { $_.TrimEnd() }
    # Добавляем пустую строку в конец, если её нет
    if ($lines.Count -eq 0 -or $lines[-1] -ne "") {
        $lines += ""
    }
    Set-Content $file $lines -Encoding UTF8
    Write-Host "Processed test file: $file"
}
Write-Host "Done: trailing whitespace removed, new line at end added for all test files." 