SpyBrain

Meditation and breathing app with guided voice, ambient music, animated backgrounds and RU/EN localization.

Key features
- Meditation tabs: Basic + Guided (TTS guidance every N seconds)
- Breathing: info bottom sheet (why/how it helps), safe Back handling
- Ambient music with Media3 + MediaSession, audio focus handling
- TTS intro and guidance with pause/resume/stop and audio focus
- Material 3 UI, Lottie backgrounds (fallback-safe)
- Per-app language (RU/EN) via AppCompatDelegate

Build
./gradlew clean :app:assembleDebug

Install
./gradlew :app:installDebug

Roadmap
- ThemePack/IconPack applied across the UI
- Full localization of all meditation cards (name/subtitle/about/howto)
- Tests: unit for TTS guidance, compose for InfoBottomSheet
- Accessibility polish

# SpyBrain - AI-Enhanced Meditation App

Инновационное приложение для медитации с искусственным интеллектом, персонализированными техниками дыхания и продвинутой аналитикой.

## 📋 Общая информация

- **Namespace:** `com.example.spybrain`
- **Версия:** 1.0 (versionCode 1)
- **Минимальный SDK:** 26 (Android 8.0)
- **Целевой SDK:** 34 (Android 14)

## 🏗 Архитектура

Проект следует принципам **Clean Architecture** с четким разделением на слои:

- **Presentation** → **Domain** → **Data**
- **Dependency Injection** через Hilt
- **MVI** паттерн для UI-слоя с базовыми компонентами (`BaseViewModel`, контракты состояний)
- **Kotlin Flow** для асинхронных операций и обновления UI

## 📂 Структура директорий

```
app/src/main/kotlin/com/example/spybrain/
│
├── data/                   # Слой данных
│   ├── model/              # Модели данных слоя data
│   ├── repository/         # Реализации репозиториев
│   ├── storage/            # DataStore и локальное хранилище
│   └── migration/          # Миграции данных
│
├── domain/                 # Бизнес-логика
│   ├── model/              # Доменные модели
│   ├── repository/         # Интерфейсы репозиториев 
│   ├── usecase/            # Use cases по фичам
│   └── error/              # Обработка ошибок
│
├── presentation/           # UI слой
│   ├── base/               # Базовые классы для UI
│   ├── navigation/         # Навигация (экраны и маршруты)
│   ├── theme/              # Темы, стили, цвета
│   ├── meditation/         # Экран медитации
│   ├── breathing/          # Экран дыхательных упражнений
│   ├── stats/              # Статистика и аналитика
│   ├── settings/           # Настройки приложения
│   ├── profile/            # Профиль пользователя
│   └── splash/             # Экран загрузки
│
├── di/                     # Dependency Injection модули
│
└── SpyBrainApp.kt          # Application класс
```

## 📱 Основные модули (фичи)

1. **Meditation** - библиотека медитаций и плеер
2. **Breathing** - интерактивные дыхательные упражнения с визуализацией
3. **Stats** - отслеживание прогресса и аналитика
4. **Profile** - управление профилем пользователя
5. **Settings** - конфигурация приложения и медитаций

## 🛠 Технический стек

### Основной:
- **Kotlin** 1.9.22
- **Gradle** 8.2.1
- **Android Gradle Plugin** 8.2.2
- **Compose Compiler** 1.5.10
- **Java** 17

### UI и архитектура:
- **Jetpack Compose** (BOM 2024.02.00)
- **Material 3** для адаптивного UI
- **Navigation Compose** 2.7.7
- **Hilt** 2.50 для DI
- **Kotlin Coroutines & Flow** 1.7.3

### Мультимедиа и данные:
- **Media3 ExoPlayer** 1.2.1
- **DataStore** 1.0.0

## 🚧 Текущий статус

Проект находится в стадии активной разработки. Базовая инфраструктура настроена, все компиляционные ошибки устранены. Основные фичи работают: медитации (ExoPlayer), дыхание (фазы + TTS), фон. музыка (AmbientMusicService), динамический фон, настройки (DataStore).

### Следующие шаги:
1. Довести UI по фичам (медитации/дыхание/настройки)
2. Подключить реальные источники данных и прогонов
3. Расширить AI-функции (советы, мотивация, голосовые сценарии)
4. Добавить интеграционные/UI-тесты
5. Оптимизация производительности и memory

## 🌍 Локализация
// TODO реализовано: Добавлены детальные TODO в strings.xml для ревизии строк и киргизской локали.
// TODO: Внедрить LocaleManager для централизованного управления локалью в приложении.
// FIXME устранено: В ресурсах обнаружена киргизская локаль. Требуется ревизия: либо добавить полноценную поддержку, либо удалить случайные строки. (см. TODO в strings.xml)
// NOTE реализовано по аудиту: Все ошибки и сообщения должны быть вынесены в strings.xml для поддержки локализации. (Добавлены TODO для проверки оставшихся хардкодов)

##  Навигация
// NOTE: Навигация приведена в порядок; MainScreen интегрирован с DynamicBackground.
// TODO: Для каждого экрана использовать уникальную иконку.

## 🧪 Тестирование
// TODO: Добавить примеры юнит-тестов для ViewModel, UseCase, Repository, Room DAO
// TODO: Добавить интеграционные тесты для взаимодействия слоёв и сервисов
// TODO: Добавить UI-тесты для пользовательских сценариев
// TODO: Описать план покрытия тестами в docs/testing.md 

## 🗃 Структура базы данных и миграции
// TODO: Описать сущности Room, связи и миграции (см. docs/database.md)
// TODO: Проверить и протестировать все миграции (MIGRATION_1_2, MIGRATION_2_3) 

## 🛰 Сервисы приложения
- AmbientMusicService — фоновая музыка (assets), поддержка громкости, fade-out, foreground уведомление
- MeditationPlayerService — воспроизведение медитаций через MediaSession
- VoiceAssistantService — TTS, голосовые подсказки и мотивация
// TODO: Документировать взаимодействие сервисов, добавить диаграмму в docs/
