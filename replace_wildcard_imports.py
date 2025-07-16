import os
import re

# Карта: wildcard -> список явных импортов (минимальный пример, расширять по необходимости)
REPLACEMENTS = {
    'androidx.compose.foundation.layout.*': [
        'androidx.compose.foundation.layout.Box',
        'androidx.compose.foundation.layout.Column',
        'androidx.compose.foundation.layout.Row',
        'androidx.compose.foundation.layout.Spacer',
        'androidx.compose.foundation.layout.fillMaxSize',
        'androidx.compose.foundation.layout.padding',
        'androidx.compose.foundation.layout.Arrangement',
        'androidx.compose.foundation.layout.Alignment',
    ],
    'androidx.compose.material3.*': [
        'androidx.compose.material3.Button',
        'androidx.compose.material3.Text',
        'androidx.compose.material3.Icon',
        'androidx.compose.material3.MaterialTheme',
        'androidx.compose.material3.Surface',
        'androidx.compose.material3.Card',
        'androidx.compose.material3.Checkbox',
        'androidx.compose.material3.Switch',
        'androidx.compose.material3.Slider',
    ],
    'androidx.compose.runtime.*': [
        'androidx.compose.runtime.Composable',
        'androidx.compose.runtime.LaunchedEffect',
        'androidx.compose.runtime.getValue',
        'androidx.compose.runtime.setValue',
        'androidx.compose.runtime.remember',
        'androidx.compose.runtime.mutableStateOf',
        'androidx.compose.runtime.collectAsState',
    ],
    'androidx.compose.material.icons.filled.*': [
        'androidx.compose.material.icons.filled.Add',
        'androidx.compose.material.icons.filled.Close',
        'androidx.compose.material.icons.filled.Menu',
        'androidx.compose.material.icons.filled.ArrowBack',
        'androidx.compose.material.icons.filled.Check',
    ],
    'androidx.compose.animation.*': [
        'androidx.compose.animation.AnimatedVisibility',
        'androidx.compose.animation.core.tween',
    ],
    'androidx.compose.animation.core.*': [
        'androidx.compose.animation.core.tween',
        'androidx.compose.animation.core.animateFloatAsState',
    ],
    'kotlin.math.*': [
        'kotlin.math.abs',
        'kotlin.math.max',
        'kotlin.math.min',
        'kotlin.math.roundToInt',
        'kotlin.math.sqrt',
    ],
    'java.util.*': [
        'java.util.Date',
        'java.util.Locale',
        'java.util.UUID',
    ],
    'io.mockk.*': [
        'io.mockk.every',
        'io.mockk.mockk',
        'io.mockk.verify',
        'io.mockk.slot',
        'io.mockk.just',
        'io.mockk.Runs',
    ],
    'kotlinx.coroutines.test.*': [
        'kotlinx.coroutines.test.runTest',
        'kotlinx.coroutines.test.TestCoroutineDispatcher',
    ],
    'androidx.compose.ui.graphics.*': [
        'androidx.compose.ui.graphics.Color',
        'androidx.compose.ui.graphics.Brush',
        'androidx.compose.ui.graphics.drawscope.DrawScope',
    ],
}

root = 'app/src'

pattern = re.compile(r'^import (.+\.\*)', re.MULTILINE)

for dirpath, _, filenames in os.walk(root):
    for filename in filenames:
        if filename.endswith('.kt'):
            path = os.path.join(dirpath, filename)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            matches = pattern.findall(content)
            changed = False
            for wc in matches:
                if wc in REPLACEMENTS:
                    # Удаляем строку с wildcard import
                    content = re.sub(rf'^import {re.escape(wc)}\s*\n', '', content, flags=re.MULTILINE)
                    # Добавляем явные импорты
                    explicit = '\n'.join(f'import {imp}' for imp in REPLACEMENTS[wc]) + '\n'
                    content = explicit + content
                    changed = True
            if changed:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f'Заменён wildcard import в {path}')
print('Завершено: wildcard imports заменены на явные.') 