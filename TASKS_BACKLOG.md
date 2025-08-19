 Блокирующие (Blocking)
 
 - Починить unit-тест VoiceAssistantService (Robolectric/абстракции Audio/TTS) — 0.5 дн
 - Унифицировать версии Media3 (убрать 1.2.0 прямые, оставить одну версию) — 0.5 дн
 - Включить exportSchema=true и сгенерировать/закоммитить схемы Room — 0.5 дн

 Крупные (Major)
 
 - Рефакторинг TTS: DI через Hilt, убрать создание в Composable, централизованный release — 1 дн
 - DI storage: не прокидывать DAO наружу, использовать репозитории — 1 дн
 - Локализация: ревизия ключей RU/EN, удаление дублей — 1 дн

 Средние (Normal)
 
 - Accessibility: contentDescription/semantics для иконок/кнопок — 0.5 дн
 - AmbientMusicService: проверки доступности аудиоэффектов и безопасные гварды — 0.5 дн
 - Нормализация кодировок комментариев (UTF-8) — 0.5 дн

 Инфраструктура (CI)
 
 - Добавить CI workflow: detekt, ktlintCheck, assembleDebug, testDebugUnitTest, публикация отчетов — 0.5–1 дн

 Тесты
 
 - Добавить unit/compose тесты: VoiceAssistant guidance режимы; InfoBottomSheet вкладки — 0.5–1 дн




