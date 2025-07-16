import os
import re

KDOC_CLASS_TEMPLATE = "/**\n * TODO: Description for {name}.\n */\n"
KDOC_FUNCTION_TEMPLATE = "/**\n * TODO: Description for {name}.\n */\n"
KDOC_PROPERTY_TEMPLATE = "/** TODO: Description for {name}. */\n"

def has_kdoc(lines, idx):
    # Проверяем, есть ли KDoc перед текущей строкой
    i = idx - 1
    while i >= 0 and lines[i].strip() == "":
        i -= 1
    return i >= 0 and lines[i].strip().startswith("/**")

def add_kdoc_to_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    new_lines = []
    class_pattern = re.compile(r'^\s*(data\s+class|sealed\s+class|enum\s+class|class|interface|object)\s+(\w+)')
    fun_pattern = re.compile(r'^\s*(suspend\s+)?fun\s+(\w+)')
    prop_pattern = re.compile(r'^\s*(val|var)\s+(\w+)')
    skip_private = re.compile(r'^\s*(private|internal|protected)\b')

    i = 0
    while i < len(lines):
        line = lines[i]

        # Пропускаем private/internal/protected
        if skip_private.match(line):
            new_lines.append(line)
            i += 1
            continue

        # Классы/интерфейсы/object/data/sealed/enum
        match = class_pattern.match(line)
        if match and not has_kdoc(lines, i):
            new_lines.append(KDOC_CLASS_TEMPLATE.format(name=match.group(2)))
            new_lines.append(line)
            i += 1
            continue

        # Функции
        match = fun_pattern.match(line)
        if match and not has_kdoc(lines, i):
            new_lines.append(KDOC_FUNCTION_TEMPLATE.format(name=match.group(2)))
            new_lines.append(line)
            i += 1
            continue

        # Свойства (только на верхнем уровне или в классе, не в теле функции)
        match = prop_pattern.match(line)
        if match and not has_kdoc(lines, i):
            new_lines.append(KDOC_PROPERTY_TEMPLATE.format(name=match.group(2)))
            new_lines.append(line)
            i += 1
            continue

        # Всё остальное — как было
        new_lines.append(line)
        i += 1

    with open(path, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)

def walk_and_add_kdoc(root_dir):
    for dirpath, _, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith('.kt'):
                file_path = os.path.join(dirpath, filename)
                add_kdoc_to_file(file_path)

if __name__ == "__main__":
    # Укажи директорию, например: "app/src/main/kotlin"
    target_dir = "app/src/main/kotlin"
    walk_and_add_kdoc(target_dir)
    print(f"KDoc автодокументирование завершено для {target_dir}!") 