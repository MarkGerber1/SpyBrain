import os
import re

KDOC_CLASS_TEMPLATE = """/**\n * TODO: Description for {name}.\n{properties} */\n"""
KDOC_FUNCTION_TEMPLATE = """/**\n * TODO: Description for {name}.\n{params}{returns} */\n"""
KDOC_PROPERTY_TEMPLATE = "/** TODO: Description for {name}. */\n"

TODO_SUMMARY_FILE = "TODO_KDOC_SUMMARY.md"

def has_kdoc(lines, idx):
    i = idx - 1
    while i >= 0 and lines[i].strip() == "":
        i -= 1
    return i >= 0 and lines[i].strip().startswith("/**")

def extract_properties_from_class(line):
    # Ищем параметры конструктора data/sealed/enum class
    m = re.search(r'\(([^)]*)\)', line)
    if not m:
        return []
    params = m.group(1)
    props = []
    for p in params.split(','):
        p = p.strip()
        if p.startswith("val ") or p.startswith("var "):
            name = p.split()[1].split(':')[0]
            props.append(name)
    return props

def extract_params_from_fun(line):
    m = re.search(r'fun\s+\w+\s*\(([^)]*)\)', line)
    if not m:
        return []
    params = m.group(1)
    result = []
    for p in params.split(','):
        p = p.strip()
        if p:
            name = p.split(':')[0].strip()
            result.append(name)
    return result

def extract_return_from_fun(line):
    m = re.search(r'fun\s+\w+\s*\([^)]*\)\s*:\s*([\w<>\[\]?]+)', line)
    if m and m.group(1) != 'Unit':
        return m.group(1)
    return None

def add_kdoc_to_file(path, todo_list):
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

        if skip_private.match(line):
            new_lines.append(line)
            i += 1
            continue

        # Классы/интерфейсы/object/data/sealed/enum
        match = class_pattern.match(line)
        if match and not has_kdoc(lines, i):
            name = match.group(2)
            props = extract_properties_from_class(line)
            prop_tags = ""
            for p in props:
                prop_tags += f" * @property {p} TODO: Description.\n"
                todo_list.append(f"{os.path.basename(path)}: {name} property {p}")
            kdoc = KDOC_CLASS_TEMPLATE.format(name=name, properties=prop_tags)
            new_lines.append(kdoc)
            todo_list.append(f"{os.path.basename(path)}: {name} class/interface/object")
            new_lines.append(line)
            i += 1
            continue

        # Функции
        match = fun_pattern.match(line)
        if match and not has_kdoc(lines, i):
            name = match.group(2)
            params = extract_params_from_fun(line)
            param_tags = ""
            for p in params:
                param_tags += f" * @param {p} TODO: Description.\n"
                todo_list.append(f"{os.path.basename(path)}: {name} param {p}")
            ret = extract_return_from_fun(line)
            return_tag = f" * @return TODO: Description.\n" if ret else ""
            if ret:
                todo_list.append(f"{os.path.basename(path)}: {name} return {ret}")
            kdoc = KDOC_FUNCTION_TEMPLATE.format(name=name, params=param_tags, returns=return_tag)
            new_lines.append(kdoc)
            todo_list.append(f"{os.path.basename(path)}: {name} function")
            new_lines.append(line)
            i += 1
            continue

        # Свойства
        match = prop_pattern.match(line)
        if match and not has_kdoc(lines, i):
            name = match.group(2)
            kdoc = KDOC_PROPERTY_TEMPLATE.format(name=name)
            new_lines.append(kdoc)
            todo_list.append(f"{os.path.basename(path)}: {name} property")
            new_lines.append(line)
            i += 1
            continue

        new_lines.append(line)
        i += 1

    with open(path, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)

def walk_and_add_kdoc(root_dir):
    todo_list = []
    for dirpath, _, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith('.kt'):
                file_path = os.path.join(dirpath, filename)
                add_kdoc_to_file(file_path, todo_list)
    # Сохраняем TODO summary
    with open(TODO_SUMMARY_FILE, 'w', encoding='utf-8') as f:
        f.write('# TODO: Description summary\n\n')
        for item in todo_list:
            f.write(f'- {item}\n')

if __name__ == "__main__":
    target_dir = "app/src/main/kotlin"
    walk_and_add_kdoc(target_dir)
    print(f"KDoc автодокументирование завершено для {target_dir}!")
    print(f"TODO summary сохранён в {TODO_SUMMARY_FILE}") 