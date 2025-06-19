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
     * Получает Vibrator с учетом версии Android
     */
    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        } catch (e: Exception) {
            Timber.e(e, "Ошибка получения Vibrator")
            null
        }
    }
    
    /**
     * Проверяет поддержку вибрации
     */
    fun hasVibrator(context: Context): Boolean {
        return getVibrator(context)?.hasVibrator() == true
    }
    
    /**
     * Вибрация для ошибок
     */
    fun vibrateError(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 200, 100, 200, 100, 200),
                    intArrayOf(0, 255, 0, 255, 0, 255),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 200, 100, 200, 100, 200), -1)
            }
            Timber.d("Вибрация ошибки выполнена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка вибрации ошибки")
        }
    }
    
    /**
     * Вибрация для успешных действий
     */
    fun vibrateSuccess(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 50, 100, 50, 100),
                    intArrayOf(0, 128, 0, 128, 0, 128),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 100, 50, 100, 50, 100), -1)
            }
            Timber.d("Вибрация успеха выполнена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка вибрации успеха")
        }
    }
    
    /**
     * Легкая вибрация для тактильной обратной связи
     */
    fun vibrateLight(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE / 2)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
            Timber.d("Легкая вибрация выполнена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка легкой вибрации")
        }
    }
    
    /**
     * Короткая вибрация для уведомлений
     */
    fun shortVibration(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
            Timber.d("Короткая вибрация выполнена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка короткой вибрации")
        }
    }
    
    /**
     * Длинная вибрация для важных событий
     */
    fun longVibration(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
            Timber.d("Длинная вибрация выполнена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка длинной вибрации")
        }
    }
    
    /**
     * Вибрация для дыхательных упражнений
     */
    fun breathingVibration(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 200, 100, 200, 100, 200),
                    intArrayOf(0, 255, 0, 255, 0, 255),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 200, 100, 200, 100, 200), -1)
            }
            Timber.d("Вибрация для дыхания выполнена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка вибрации для дыхания")
        }
    }
    
    /**
     * Вибрация для медитации
     */
    fun meditationVibration(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 300, 200, 300, 200, 300),
                    intArrayOf(0, 128, 0, 128, 0, 128),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 300, 200, 300, 200, 300), -1)
            }
            Timber.d("Вибрация для медитации выполнена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка вибрации для медитации")
        }
    }
    
    /**
     * Вибрация для достижений
     */
    fun achievementVibration(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 50, 100, 50, 100, 50, 100),
                    intArrayOf(0, 255, 0, 255, 0, 255, 0, 255),
                    -1
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 100, 50, 100, 50, 100, 50, 100), -1)
            }
            Timber.d("Вибрация для достижений выполнена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка вибрации для достижений")
        }
    }
    
    /**
     * Останавливает вибрацию
     */
    fun stopVibration(context: Context) {
        val vibrator = getVibrator(context) ?: return
        
        try {
            vibrator.cancel()
            Timber.d("Вибрация остановлена")
        } catch (e: Exception) {
            Timber.e(e, "Ошибка остановки вибрации")
        }
    }
} 