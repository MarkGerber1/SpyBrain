package com.example.spybrain.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import timber.log.Timber

/**
 * Утилита для тактильной обратной связи
 * Предоставляет современные вибрации для разных действий в приложении
 */
object VibrationUtil {
    
    /**
     * Легкая вибрация для UI взаимодействий
     */
    fun vibrateLight(context: Context) {
        try {
            val vibrator = getVibrator(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to vibrate light")
        }
    }
    
    /**
     * Вибрация для успешных действий
     */
    fun vibrateSuccess(context: Context) {
        try {
            val vibrator = getVibrator(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 50, 100, 50),
                    intArrayOf(0, 100, 0, 100),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 50, 100, 50), -1)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to vibrate success")
        }
    }
    
    /**
     * Вибрация для ошибок
     */
    fun vibrateError(context: Context) {
        try {
            val vibrator = getVibrator(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 50, 100, 50, 100),
                    intArrayOf(0, 150, 0, 150, 0, 150),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 100, 50, 100, 50, 100), -1)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to vibrate error")
        }
    }
    
    /**
     * Вибрация для дыхательных упражнений
     */
    fun vibrateBreathing(context: Context, phase: BreathingPhase) {
        try {
            val vibrator = getVibrator(context)
            when (phase) {
                BreathingPhase.INHALE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val effect = VibrationEffect.createOneShot(200, 80)
                        vibrator.vibrate(effect)
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(200)
                    }
                }
                BreathingPhase.EXHALE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val effect = VibrationEffect.createOneShot(300, 60)
                        vibrator.vibrate(effect)
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(300)
                    }
                }
                BreathingPhase.HOLD -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val effect = VibrationEffect.createOneShot(100, 40)
                        vibrator.vibrate(effect)
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(100)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to vibrate breathing")
        }
    }
    
    /**
     * Вибрация для медитации
     */
    fun vibrateMeditation(context: Context) {
        try {
            val vibrator = getVibrator(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 1000, 500),
                    intArrayOf(0, 30, 0, 30),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 500, 1000, 500), -1)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to vibrate meditation")
        }
    }
    
    /**
     * Вибрация для достижений
     */
    fun vibrateAchievement(context: Context) {
        try {
            val vibrator = getVibrator(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 200, 100, 200, 100, 200),
                    intArrayOf(0, 120, 0, 120, 0, 120, 0),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 100, 200, 100, 200, 100, 200), -1)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to vibrate achievement")
        }
    }
    
    /**
     * Получение Vibrator с учетом версии Android
     */
    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    enum class BreathingPhase {
        INHALE, EXHALE, HOLD
    }
} 