package com.example.spybrain.util

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.spybrain.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Фасад для управления фонами приложения.
 * Координирует живые фоны и видео-фоны в зависимости от настроек пользователя.
 */
@Singleton
class BackgroundManager @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /** Текущий активный фон. */
    private var currentBackground: BackgroundType? = null

    /** Слушатель готовности видео-плеера. */
    private var videoPlayerListener: VideoPlayerListener? = null

    /** Текущий пресет фона из настроек. */
    private var currentPreset: String = "nature"

    init {
        Timber.d("BackgroundManager инициализирован")
    }

    /**
     * Инициализирует менеджер фонов.
     * Должен вызываться при старте приложения.
     */
    fun initialize() {
        Timber.d("Инициализация BackgroundManager")

        // Подписываемся на изменения настроек фона
        scope.launch {
            settingsRepository.backgroundPresetFlow.collectLatest { preset ->
                currentPreset = preset
                applyBackgroundPreset(preset)
            }
        }
    }

    /**
     * Подключает менеджер к жизненному циклу активити/фрагмента.
     * Автоматически синхронизирует фон при старте.
     */
    fun bindToLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        Timber.d("BackgroundManager привязан к жизненному циклу")
    }

    /**
     * Применяет пресет фона.
     *
     * @param preset Название пресета фона.
     */
    private fun applyBackgroundPreset(preset: String) {
        Timber.d("Применение пресета фона: $preset")

        when {
            preset.startsWith("video_") -> {
                // Видео-фон
                val videoId = preset.removePrefix("video_")
                applyVideoBackground(videoId)
            }
            else -> {
                // Живой фон
                applyLiveBackground(preset)
            }
        }
    }

    /**
     * Применяет видео-фон.
     *
     * @param videoId Идентификатор видео.
     */
    private fun applyVideoBackground(videoId: String) {
        Timber.d("Применение видео-фона: $videoId")

        // Проверяем готовность видео-плеера
        if (videoPlayerListener?.isReady() == true) {
            // Видео-плеер готов, применяем видео-фон
            videoPlayerListener?.playVideo(videoId)
            currentBackground = BackgroundType.Video(videoId)
            Timber.d("Видео-фон применен успешно: $videoId")
        } else {
            // Видео-плеер не готов, используем живой фон как fallback
            Timber.w("Видео-плеер не готов, используем живой фон как fallback")
            applyLiveBackground("nature")
            currentBackground = BackgroundType.Live("nature")

            // Ждем готовности видео-плеера для последующего применения
            waitForVideoPlayerAndApply(videoId)
        }
    }

    /**
     * Применяет живой фон.
     *
     * @param backgroundType Тип живого фона.
     */
    private fun applyLiveBackground(backgroundType: String) {
        Timber.d("Применение живого фона: $backgroundType")

        // Здесь должна быть логика применения живого фона
        // Например, установка цвета или градиента для Compose

        currentBackground = BackgroundType.Live(backgroundType)
    }

    /**
     * Ожидает готовности видео-плеера и применяет видео-фон.
     *
     * @param videoId Идентификатор видео.
     */
    private fun waitForVideoPlayerAndApply(videoId: String) {
        scope.launch {
            try {
                // Ждем готовности видео-плеера (до 10 секунд)
                var attempts = 0
                while (attempts < 50 && videoPlayerListener?.isReady() != true) {
                    kotlinx.coroutines.delay(200)
                    attempts++
                }

                if (videoPlayerListener?.isReady() == true) {
                    Timber.d("Видео-плеер готов, применяем видео-фон: $videoId")
                    videoPlayerListener?.playVideo(videoId)
                    currentBackground = BackgroundType.Video(videoId)
                } else {
                    Timber.w("Видео-плеер не готов после ожидания, остаемся на живом фоне")
                }
            } catch (e: Exception) {
                Timber.e(e, "Ошибка ожидания видео-плеера")
            }
        }
    }

    /**
     * Устанавливает слушатель видео-плеера.
     *
     * @param listener Слушатель видео-плеера.
     */
    fun setVideoPlayerListener(listener: VideoPlayerListener?) {
        videoPlayerListener = listener
        Timber.d("Установлен слушатель видео-плеера: $listener")
    }

    /**
     * Получает текущий активный фон.
     *
     * @return Текущий фон или null если не установлен.
     */
    fun getCurrentBackground(): BackgroundType? = currentBackground

    override fun onStart(owner: LifecycleOwner) {
        Timber.d("BackgroundManager: onStart")
        // При старте активити пересверяем синхронизацию фона
        resyncBackground()
    }

    /**
     * Пересинхронизирует фон с настройками.
     * Вызывается при старте активити для корректного применения фона.
     */
    private fun resyncBackground() {
        Timber.d("Пересинхронизация фона с настройками")
        applyBackgroundPreset(currentPreset)
    }

    override fun onStop(owner: LifecycleOwner) {
        Timber.d("BackgroundManager: onStop")
        // Здесь можно добавить логику паузы видео-фона
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Timber.d("BackgroundManager: onDestroy")
        scope.launch {
            // Освобождаем ресурсы при уничтожении
            videoPlayerListener?.stop()
        }
    }

    /**
     * Пересинхронизирует фон с настройками.
     * Вызывается при старте активити для корректного применения фона.
     */
    private fun resyncBackground() {
        Timber.d("Пересинхронизация фона с настройками")
        applyBackgroundPreset(currentPreset)
    }

    /**
     * Интерфейс слушателя видео-плеера.
     */
    interface VideoPlayerListener {
        /** Проверяет готовность видео-плеера. */
        fun isReady(): Boolean

        /** Воспроизводит видео с указанным идентификатором. */
        fun playVideo(videoId: String)

        /** Останавливает видео-плеер. */
        fun stop()
    }

    /**
     * Типы фонов.
     */
    sealed class BackgroundType {
        /** Живой фон. */
        data class Live(val type: String) : BackgroundType()

        /** Видео-фон. */
        data class Video(val videoId: String) : BackgroundType()
    }
}