# 🚀 План реализации отстающих элементов и новых фич SpyBrain

## 📋 Обзор плана

**Срок реализации**: 8 недель (Неделя 1-2: Базовые исправления, Неделя 3-4: Архитектурные улучшения, Неделя 5-8: Новые фичи)

**Команда**: 2-3 разработчика Android/Kotlin + 1 QA инженер

**Приоритет**: Критично → Важно → Желательно

---

## 🏃‍♂️ Этап 1: Исправление критических проблем (Недели 1-2)

### 🎯 Задача 1.1: Исправление падающих тестов (23/50)

**Статус**: 🔴 Критично | **Срок**: 2 дня | **Исполнитель**: Android Developer

#### Проблемные тесты:
1. **SettingsViewModelTest.kt** - Неинициализированные MockK аннотации
2. **HeartRateViewModelTest.kt** - Неправильные ожидания в тестах
3. **SettingsDataStoreTest.kt** - Требуется реальный Context

#### План исправления:

**День 1: SettingsViewModelTest**
```kotlin
// Исправление в SettingsViewModelTest.kt
@Before
fun setup() {
    MockKAnnotations.init(this) // ← Добавить эту строку
    Dispatchers.setMain(StandardTestDispatcher())

    // Моки должны быть настроены правильно
    every { settingsDataStore.themeFlow } returns flowOf("nature")
    every { settingsDataStore.ambientEnabledFlow } returns flowOf(false)
    // ... остальные моки
}
```

**День 1: HeartRateViewModelTest**
```kotlin
// Исправить ожидания в тестах
@Test
fun `should show loading state initially`() = runTest {
    // Given
    coEvery { heartRateRepository.getHeartRate() } returns flowOf(Loading)

    // When
    initViewModel()

    // Then
    assertEquals(Loading, viewModel.uiState.value.heartRateState)
}
```

**День 2: SettingsDataStoreTest**
```kotlin
// Альтернатива: Создать тестовую версию DataStore
class TestSettingsDataStore(context: Context) : SettingsDataStore(context) {
    override suspend fun setTheme(theme: String) {
        // Заглушка для тестов
    }
}
```

#### Метрики успеха:
- ✅ Все 50 тестов проходят
- ✅ Покрытие тестами > 80%
- ✅ Нет flaky тестов

---

## 🏗️ Этап 2: Архитектурные улучшения (Недели 3-4)

### 🎯 Задача 2.1: Консолидация сервисов

**Статус**: 🟡 Важно | **Срок**: 5 дней | **Исполнитель**: Senior Android Developer

#### Текущая проблема:
```kotlin
// 9 сервисов для простой функциональности
- MeditationPlayerService    ← основной плеер
- AmbientMusicService        ← фоновая музыка
- BackgroundMusicService     ← музыка в фоне
- HealthAdvisorService       ← советы по здоровью
- AiMentorService           ← ИИ-наставник
- VoiceAssistantService     ← голосовой ассистент
- ReminderService           ← напоминания
- ReminderReceiver          ← приемник напоминаний
- MeditationPlayerReceiver  ← приемник плеера
```

#### Новая архитектура:
```kotlin
// Консолидированная структура (3 основных сервиса)
interface MediaService {
    fun playMeditation(url: String)
    fun playAmbient(trackId: String)
    fun stopAll()
    fun setVolume(level: Float)
}

interface NotificationService {
    fun scheduleReminder(reminder: Reminder)
    fun cancelReminder(id: String)
    fun showMeditationNotification(title: String)
}

interface AiService {
    suspend fun generatePersonalProgram(preferences: UserPreferences): MeditationProgram
    suspend fun analyzeSession(session: Session): AiInsights
    fun getVoiceGuidance(text: String): AudioGuidance
}
```

#### Шаги реализации:

**День 1-2: Создание интерфейсов**
```kotlin
// Создать базовые интерфейсы в domain/service/
interface MediaService
interface NotificationService
interface AiService
```

**День 3-4: Реализация сервисов**
```kotlin
// Объединить MeditationPlayerService + AmbientMusicService + BackgroundMusicService
class MediaServiceImpl @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val notificationManager: NotificationManager
) : MediaService {
    override fun playMeditation(url: String) {
        // Общая логика воспроизведения
    }

    override fun playAmbient(trackId: String) {
        // Логика фоновой музыки
    }
}
```

**День 5: Миграция клиентов**
```kotlin
// Обновить DI модули для использования новых сервисов
@Module
object ServiceModule {
    @Provides
    fun provideMediaService(impl: MediaServiceImpl): MediaService = impl

    @Provides
    fun provideNotificationService(impl: NotificationServiceImpl): NotificationService = impl

    @Provides
    fun provideAiService(impl: AiServiceImpl): AiService = impl
}
```

#### Метрики успеха:
- ✅ Количество сервисов уменьшено с 9 до 3-4
- ✅ Логика распределена правильно
- ✅ Все клиенты мигрированы

---

### 🎯 Задача 2.2: Реализация стратегии кеширования

**Статус**: 🟡 Важно | **Срок**: 4 дня | **Исполнитель**: Android Developer

#### План реализации:

**День 1: Создание инфраструктуры кеширования**
```kotlin
// domain/cache/CacheStrategy.kt
interface CacheStrategy<T> {
    suspend fun getOrFetch(key: String, fetcher: suspend () -> T): T
    suspend fun invalidate(key: String)
    suspend fun clearAll()
}

// data/cache/InMemoryCache.kt
class InMemoryCache<K, V>(
    private val maxSize: Int = 100,
    private val ttlMillis: Long = 5 * 60 * 1000L // 5 минут
) : CacheStrategy<V> {
    private val cache = LinkedHashMap<K, CacheEntry<V>>()

    override suspend fun getOrFetch(key: K, fetcher: suspend () -> V): V {
        // Реализация кеширования в памяти
    }
}
```

**День 2-3: Интеграция с репозиториями**
```kotlin
// data/repository/MeditationRepositoryImpl.kt
class MeditationRepositoryImpl @Inject constructor(
    private val apiService: MeditationApiService,
    private val cacheStrategy: CacheStrategy<List<Meditation>>,
    private val dao: MeditationDao
) : MeditationRepository {

    override fun getMeditations(): Flow<List<Meditation>> = flow {
        val cached = dao.getAllMeditations()
        if (cached.isNotEmpty()) {
            emit(cached)
        }

        // Получение свежих данных с кешированием
        val freshData = cacheStrategy.getOrFetch("meditations") {
            apiService.getMeditations()
        }

        dao.insertAll(freshData)
        emit(freshData)
    }
}
```

**День 4: Настройка политик кеширования**
```kotlin
// di/CacheModule.kt
@Module
object CacheModule {
    @Provides
    @Singleton
    fun provideMeditationCache(): CacheStrategy<List<Meditation>> {
        return InMemoryCache(maxSize = 50, ttlMillis = 10 * 60 * 1000L) // 10 мин
    }

    @Provides
    @Singleton
    fun provideUserDataCache(): CacheStrategy<UserProfile> {
        return DiskCache(maxSize = 10, ttlMillis = 60 * 60 * 1000L) // 1 час
    }
}
```

---

### 🎯 Задача 2.3: Улучшенная обработка ошибок

**Статус**: 🟡 Важно | **Срок**: 3 дня | **Исполнитель**: Android Developer

#### План реализации:

**День 1: Создание системы ошибок**
```kotlin
// domain/error/AppError.kt
sealed class AppError {
    data class NetworkError(
        val code: Int,
        val message: String
    ) : AppError()

    data class AudioError(
        val reason: String,
        val canRetry: Boolean = true
    ) : AppError()

    data class StorageError(
        val cause: Throwable,
        val operation: String
    ) : AppError()

    data class ValidationError(
        val field: String,
        val reason: String
    ) : AppError()

    object UnknownError : AppError()
}

// domain/error/ErrorHandler.kt
class ErrorHandler @Inject constructor() {
    fun handleError(error: AppError): ErrorState {
        return when (error) {
            is NetworkError -> ErrorState.NetworkError(error.message)
            is AudioError -> ErrorState.AudioError(error.reason, error.canRetry)
            is StorageError -> ErrorState.StorageError(error.operation)
            is ValidationError -> ErrorState.ValidationError(error.field, error.reason)
            UnknownError -> ErrorState.UnknownError
        }
    }
}
```

**День 2: Интеграция в Use Cases**
```kotlin
// domain/usecase/GetMeditationsUseCase.kt
class GetMeditationsUseCase @Inject constructor(
    private val repository: MeditationRepository,
    private val errorHandler: ErrorHandler
) {
    operator fun invoke(): Flow<DataState<List<Meditation>>> = flow {
        try {
            emit(DataState.Loading)
            repository.getMeditations().collect { meditations ->
                emit(DataState.Success(meditations))
            }
        } catch (e: Exception) {
            val appError = when (e) {
                is IOException -> AppError.NetworkError(0, e.message ?: "Network error")
                else -> AppError.UnknownError
            }
            emit(DataState.Error(errorHandler.handleError(appError)))
        }
    }
}
```

**День 3: UI обработка ошибок**
```kotlin
// presentation/base/ErrorComposable.kt
@Composable
fun ErrorDisplay(
    errorState: ErrorState,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    when (errorState) {
        is ErrorState.NetworkError -> NetworkErrorDialog(
            message = errorState.message,
            onRetry = onRetry
        )
        is ErrorState.AudioError -> AudioErrorDialog(
            reason = errorState.reason,
            canRetry = errorState.canRetry,
            onRetry = if (errorState.canRetry) onRetry else null
        )
        // ... другие типы ошибок
    }
}
```

---

### 🎯 Задача 2.4: Документация

**Статус**: 🟢 Желательно | **Срок**: 3 дня | **Исполнитель**: Technical Writer

#### План создания документации:

**День 1: API документация**
```kotlin
// Документирование публичных API
/**
 * Service for managing meditation playback and ambient music.
 *
 * This service provides a unified interface for all audio-related operations
 * in the SpyBrain application, including meditation sessions, ambient sounds,
 * and background music.
 *
 * @see MediaServiceImpl for implementation details
 * @see AudioConfiguration for configuration options
 */
interface MediaService {
    /**
     * Starts playing a meditation session.
     *
     * @param url The URL or asset path of the meditation audio file
     * @param config Configuration options for playback
     * @return PlayState indicating the current playback state
     */
    suspend fun playMeditation(url: String, config: AudioConfiguration): PlayState
}
```

**День 2: Архитектурная документация**
```markdown
# Архитектура SpyBrain

## Обзор
SpyBrain использует Clean Architecture с разделением на слои:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Presentation  │    │      Domain      │    │       Data      │
│   (Compose UI)  │◄──►│  (Use Cases)     │◄──►│ (Repositories)  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│     Services    │    │   Error Handler  │    │   Data Store    │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## Поток данных
1. UI событие → ViewModel → Use Case → Repository → Data Source
2. Обратный поток: Data Source → Repository → Use Case → ViewModel → UI
```

**День 3: Руководства пользователя**
```markdown
# Руководство по новым фичам

## AI Наставник
1. Откройте раздел "Медитация"
2. Выберите "AI Программа"
3. Укажите предпочтения (длительность, цель, опыт)
4. Получите персональную программу

## Биосинхронизация
1. Подключите устройство слежения за пульсом
2. Начните сессию дыхания
3. Приложение автоматически адаптируется под ваш ритм
```

---

## 🚀 Этап 3: Новые инновационные фичи (Недели 5-8)

### 🎯 Задача 3.1: AI Наставник с персонализацией

**Статус**: 🔴 Критично для конкурентного преимущества | **Срок**: 2 недели | **Исполнитель**: ML/Android Developer

#### Архитектура AI Наставника:

```kotlin
// domain/service/AiMentorService.kt
interface AiMentorService {
    suspend fun analyzeUserPreferences(userId: String): UserPreferences
    suspend fun generatePersonalProgram(
        preferences: UserPreferences,
        skillLevel: SkillLevel,
        availableTime: Duration
    ): MeditationProgram

    suspend fun adaptProgramBasedOnProgress(
        currentProgram: MeditationProgram,
        userProgress: List<Session>
    ): MeditationProgram

    suspend fun provideRealTimeGuidance(
        currentState: MeditationState
    ): AiGuidance
}
```

#### Шаги реализации:

**Неделя 1: Базовая инфраструктура**
```kotlin
// День 1-2: Модели данных
data class UserPreferences(
    val preferredDuration: Duration = 10.minutes,
    val meditationGoals: List<MeditationGoal> = listOf(RELAXATION),
    val preferredTimeOfDay: List<TimeOfDay> = listOf(MORNING, EVENING),
    val experienceLevel: ExperienceLevel = BEGINNER,
    val preferredStyle: List<MeditationStyle> = listOf(GUIDED, SILENT)
)

data class MeditationProgram(
    val id: String,
    val name: String,
    val description: String,
    val sessions: List<MeditationSession>,
    val aiGenerated: Boolean = true,
    val adaptability: AdaptabilitySettings
)

// День 3-4: Сервис AI анализа
class AiMentorServiceImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val meditationRepository: MeditationRepository,
    private val mlService: MLService
) : AiMentorService {

    override suspend fun analyzeUserPreferences(userId: String): UserPreferences {
        val userHistory = userRepository.getUserHistory(userId)
        val patterns = mlService.analyzePatterns(userHistory)

        return UserPreferences(
            preferredDuration = patterns.averageSessionDuration,
            meditationGoals = patterns.dominantGoals,
            experienceLevel = patterns.calculatedLevel
        )
    }

    override suspend fun generatePersonalProgram(
        preferences: UserPreferences,
        skillLevel: SkillLevel,
        availableTime: Duration
    ): MeditationProgram {
        val template = selectOptimalTemplate(preferences, skillLevel)
        return customizeTemplate(template, preferences, availableTime)
    }
}
```

**Неделя 2: Интеграция и UI**
```kotlin
// День 5-7: ViewModel для AI Наставника
@HiltViewModel
class AiMentorViewModel @Inject constructor(
    private val aiMentorService: AiMentorService,
    private val userRepository: UserRepository
) : BaseViewModel<AiMentorState, AiMentorEvent>() {

    override fun onEvent(event: AiMentorEvent) {
        when (event) {
            is AiMentorEvent.GenerateProgram -> generatePersonalProgram(event.preferences)
            is AiMentorEvent.AnalyzeProgress -> analyzeUserProgress()
        }
    }

    private fun generatePersonalProgram(preferences: UserPreferences) {
        viewModelScope.launch {
            _uiState.value = AiMentorState.Loading

            try {
                val program = aiMentorService.generatePersonalProgram(
                    preferences = preferences,
                    skillLevel = calculateSkillLevel(),
                    availableTime = preferences.preferredDuration
                )

                _uiState.value = AiMentorState.Success(program)
            } catch (e: Exception) {
                _uiState.value = AiMentorState.Error("Не удалось сгенерировать программу")
            }
        }
    }
}

// День 8-10: UI компоненты
@Composable
fun AiMentorScreen(
    viewModel: AiMentorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "AI Наставник",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (state) {
            is AiMentorState.Loading -> LoadingIndicator()
            is AiMentorState.Success -> ProgramDisplay((state as AiMentorState.Success).program)
            is AiMentorState.Error -> ErrorDisplay((state as AiMentorState.Error).message)
        }
    }
}
```

**Неделя 2: Тестирование и улучшения**
```kotlin
// День 11-14: Тестирование и оптимизация
@Test
fun `should generate program based on user preferences`() = runTest {
    val preferences = UserPreferences(
        preferredDuration = 15.minutes,
        meditationGoals = listOf(RELAXATION, FOCUS),
        experienceLevel = INTERMEDIATE
    )

    val program = aiMentorService.generatePersonalProgram(
        preferences = preferences,
        skillLevel = INTERMEDIATE,
        availableTime = 15.minutes
    )

    assertEquals(15.minutes, program.totalDuration)
    assertTrue(program.sessions.any { it.type == RELAXATION })
    assertTrue(program.sessions.any { it.type == FOCUS })
}
```

---

### 🎯 Задача 3.2: Биосинхронизация в реальном времени

**Статус**: 🔴 Критично для инновационности | **Срок**: 2 недели | **Исполнитель**: Hardware/ML Developer

#### Архитектура Биосинхронизации:

```kotlin
// domain/service/BioSyncService.kt
interface BioSyncService {
    fun startBioSync(): Flow<BioSyncState>
    fun stopBioSync()
    fun calibrate(baselineHeartRate: Int)
    fun setSensitivity(level: SensitivityLevel)
    fun getOptimalPatternForHeartRate(currentHR: Int): BreathingPattern
}
```

#### Шаги реализации:

**Неделя 1: Интеграция с сенсорами**
```kotlin
// День 1-3: Сервис работы с сенсорами
class HeartRateSensorManager @Inject constructor(
    private val context: Context,
    private val permissionsManager: PermissionsManager
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    fun monitor(): Flow<Int> = callbackFlow {
        if (!permissionsManager.hasBodySensorsPermission()) {
            throw SecurityException("Body sensors permission required")
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val heartRate = event.values[0].toInt()
                trySend(heartRate)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Обработка изменения точности
            }
        }

        sensorManager.registerListener(listener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

// День 4-5: Алгоритм адаптации
class BreathingPatternAdapter @Inject constructor(
    private val mlService: MLService
) {
    fun adaptPatternForHeartRate(
        basePattern: BreathingPattern,
        currentHR: Int,
        targetHR: Int
    ): BreathingPattern {
        val hrDifference = currentHR - targetHR
        val adaptationFactor = calculateAdaptationFactor(hrDifference)

        return basePattern.copy(
            inhaleDuration = (basePattern.inhaleDuration * adaptationFactor).toInt(),
            exhaleDuration = (basePattern.exhaleDuration * adaptationFactor).toInt(),
            holdDuration = (basePattern.holdDuration * adaptationFactor).toInt()
        )
    }

    private fun calculateAdaptationFactor(hrDifference: Int): Double {
        return when {
            hrDifference > 10 -> 1.2  // Успокоить
            hrDifference < -10 -> 0.8 // Активировать
            else -> 1.0               // Оптимально
        }
    }
}
```

**Неделя 2: Интеграция и UI**
```kotlin
// День 6-8: Основной сервис биосинхронизации
class BioSyncServiceImpl @Inject constructor(
    private val heartRateSensor: HeartRateSensorManager,
    private val patternAdapter: BreathingPatternAdapter,
    private val breathingCoach: BreathingCoach
) : BioSyncService {

    private val _bioSyncState = MutableStateFlow<BioSyncState>(BioSyncState.Idle)
    val bioSyncState: StateFlow<BioSyncState> = _bioSyncState.asStateFlow()

    override fun startBioSync(): Flow<BioSyncState> = flow {
        emit(BioSyncState.Calibrating)

        // Калибровка базового ритма сердца
        val baselineHR = calibrateBaseline()

        emit(BioSyncState.Active(baselineHR))

        // Мониторинг и адаптация в реальном времени
        heartRateSensor.monitor()
            .combine(breathingCoach.currentPattern) { hr, pattern ->
                val adaptedPattern = patternAdapter.adaptPatternForHeartRate(
                    pattern, hr, baselineHR
                )
                breathingCoach.applyPattern(adaptedPattern)
            }
            .collect { adaptedPattern ->
                _bioSyncState.value = BioSyncState.Adapting(adaptedPattern)
            }
    }

    private suspend fun calibrateBaseline(): Int {
        return heartRateSensor.monitor()
            .take(30) // 30 секунд калибровки
            .average()
            .toInt()
    }
}

// День 9-11: ViewModel для биосинхронизации
@HiltViewModel
class BioSyncViewModel @Inject constructor(
    private val bioSyncService: BioSyncService
) : BaseViewModel<BioSyncState, BioSyncEvent>() {

    private var bioSyncJob: Job? = null

    override fun onEvent(event: BioSyncEvent) {
        when (event) {
            BioSyncEvent.StartSync -> startBioSync()
            BioSyncEvent.StopSync -> stopBioSync()
            is BioSyncEvent.SetSensitivity -> setSensitivity(event.level)
        }
    }

    private fun startBioSync() {
        bioSyncJob?.cancel()
        bioSyncJob = viewModelScope.launch {
            bioSyncService.startBioSync().collect { state ->
                _uiState.value = state
            }
        }
    }
}

// День 12-14: UI биосинхронизации
@Composable
fun BioSyncScreen(
    viewModel: BioSyncViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Биосинхронизация",
            style = MaterialTheme.typography.h4
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            BioSyncState.Idle -> StartBioSyncButton(onClick = {
                viewModel.onEvent(BioSyncEvent.StartSync)
            })

            BioSyncState.Calibrating -> CalibratingDisplay()

            is BioSyncState.Active -> ActiveSyncDisplay(
                baselineHR = (state as BioSyncState.Active).baselineHR
            )

            is BioSyncState.Adapting -> AdaptingDisplay(
                pattern = (state as BioSyncState.Adapting).currentPattern,
                currentHR = (state as BioSyncState.Adapting).currentHR
            )
        }
    }
}
```

---

## 📊 Метрики успеха и контроль качества

### Метрики по завершению проекта:
- ✅ Все 50+ тестов проходят
- ✅ Покрытие тестами > 80%
- ✅ Количество сервисов уменьшено на 60%
- ✅ Время загрузки приложения < 2 секунд
- ✅ Поддержка оффлайн режима для основных функций
- ✅ AI Наставник генерирует персональные программы
- ✅ Биосинхронизация адаптируется в реальном времени

### Контроль качества:
- **Еженедельные спринты** с демо-фич
- **Code Review** для всех изменений
- **Автоматическое тестирование** на каждый PR
- **Performance мониторинг** с Firebase
- **Пользовательское тестирование** новых фич

---

## 💰 Бюджет и ресурсы

| Ресурс | Стоимость | Обоснование |
|--------|-----------|-------------|
| Разработчики (2 чел) | 800,000 ₽ | 8 недель * 100,000 ₽/неделя |
| QA инженер | 200,000 ₽ | Тестирование и контроль качества |
| Дизайнер | 150,000 ₽ | UI/UX для новых фич |
| Серверная инфраструктура | 50,000 ₽ | AI сервисы и аналитика |
| Лицензии и инструменты | 30,000 ₽ | ML библиотеки, мониторинг |
| **Итого** | **1,230,000 ₽** | 2 месяца разработки |

---

## 🚦 Риски и mitigation стратегии

| Риск | Вероятность | Влияние | Стратегия |
|------|-------------|---------|-----------|
| Техническая сложность AI | Высокая | Высокое | Начать с MVP версии, постепенное улучшение |
| Проблемы с сенсорами | Средняя | Среднее | Фоллбек на ручной ввод данных |
| Отставание от графика | Средняя | Высокое | Еженедельный контроль прогресса |
| Проблемы с производительностью | Низкая | Высокое | Раннее профилирование |

---

## 🎯 Заключение

Этот план обеспечит **полную трансформацию SpyBrain** из хорошего приложения в **инновационный продукт мирового уровня**. Фокус на AI-наставнике и биосинхронизации создаст уникальное конкурентное преимущество на рынке wellness-приложений.

**Ключ к успеху**: Постепенная реализация с постоянным тестированием и итеративными улучшениями.