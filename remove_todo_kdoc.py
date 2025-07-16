import os
import re

root = 'app/src/main/kotlin'
todo_pattern = re.compile(r'TODO: Description')
changed_files = []
removed_lines = 0

for dirpath, _, filenames in os.walk(root):
    for filename in filenames:
        if filename.endswith('.kt'):
            path = os.path.join(dirpath, filename)
            with open(path, 'r', encoding='utf-8') as f:
                lines = f.readlines()
            new_lines = []
            file_changed = False
            for l in lines:
                if 'TODO: Description' in l:
                    print(f'Удаляю строку в {path}: {l.strip()}')
                    removed_lines += 1
                    file_changed = True
                else:
                    new_lines.append(l)
            if file_changed:
                with open(path, 'w', encoding='utf-8') as f:
                    f.writelines(new_lines)
                changed_files.append(path)

print(f'Обработано файлов: {len(changed_files)}')
print(f'Удалено строк с TODO: Description: {removed_lines}')
if changed_files:
    print('Изменённые файлы:')
    for f in changed_files:
        print(f)
else:
    print('TODO: Description не найдено или уже удалено.') 