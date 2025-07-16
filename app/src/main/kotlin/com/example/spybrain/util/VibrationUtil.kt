package com.example.spybrain.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import timber.log.Timber

/**
 * РЈС‚РёР»РёС‚Р° РґР»СЏ С‚Р°РєС‚РёР»СЊРЅРѕР№ РѕР±СЂР°С‚РЅРѕР№ СЃРІСЏР·Рё
 * РџСЂРµРґРѕСЃС‚Р°РІР»СЏРµС‚ СЃРѕРІСЂРµРјРµРЅРЅС‹Рµ РІРёР±СЂР°С†РёРё РґР»СЏ СЂР°Р·РЅС‹С… РґРµР№СЃС‚РІРёР№ РІ РїСЂРёР»РѕР¶РµРЅРёРё
 */
object VibrationUtil {

    /**
     * РџРѕР»СѓС‡Р°РµС‚ Vibrator СЃ СѓС‡РµС‚РѕРј РІРµСЂСЃРёРё Android
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
            Timber.e(e, "РћС€РёР±РєР° РїРѕР»СѓС‡РµРЅРёСЏ Vibrator")
            null
        }
    }

    /**
     * РџСЂРѕРІРµСЂСЏРµС‚ РЅР°Р»РёС‡РёРµ РІРёР±СЂР°С‚РѕСЂР° РЅР° СѓСЃС‚СЂРѕР№СЃС‚РІРµ.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
     * @return true, РµСЃР»Рё РІРёР±СЂР°С‚РѕСЂ РµСЃС‚СЊ.
     */
    fun hasVibrator(context: Context): Boolean {
        return getVibrator(context)?.hasVibrator() == true
    }

    /**
     * Р’РёР±СЂР°С†РёСЏ РґР»СЏ РѕС€РёР±РєРё.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
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
            Timber.d("Р’РёР±СЂР°С†РёСЏ РѕС€РёР±РєРё РІС‹РїРѕР»РЅРµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РІРёР±СЂР°С†РёРё РѕС€РёР±РєРё")
        }
    }

    /**
     * Р’РёР±СЂР°С†РёСЏ РґР»СЏ СѓСЃРїРµС…Р°.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
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
            Timber.d("Р’РёР±СЂР°С†РёСЏ СѓСЃРїРµС…Р° РІС‹РїРѕР»РЅРµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РІРёР±СЂР°С†РёРё СѓСЃРїРµС…Р°")
        }
    }

    /**
     * Р›С‘РіРєР°СЏ РІРёР±СЂР°С†РёСЏ.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
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
            Timber.d("Р›РµРіРєР°СЏ РІРёР±СЂР°С†РёСЏ РІС‹РїРѕР»РЅРµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° Р»РµРіРєРѕР№ РІРёР±СЂР°С†РёРё")
        }
    }

    /**
     * РљРѕСЂРѕС‚РєР°СЏ РІРёР±СЂР°С†РёСЏ.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
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
            Timber.d("РљРѕСЂРѕС‚РєР°СЏ РІРёР±СЂР°С†РёСЏ РІС‹РїРѕР»РЅРµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РєРѕСЂРѕС‚РєРѕР№ РІРёР±СЂР°С†РёРё")
        }
    }

    /**
     * Р”Р»РёРЅРЅР°СЏ РІРёР±СЂР°С†РёСЏ.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
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
            Timber.d("Р”Р»РёРЅРЅР°СЏ РІРёР±СЂР°С†РёСЏ РІС‹РїРѕР»РЅРµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РґР»РёРЅРЅРѕР№ РІРёР±СЂР°С†РёРё")
        }
    }

    /**
     * Р’РёР±СЂР°С†РёСЏ РґР»СЏ РґС‹С…Р°С‚РµР»СЊРЅС‹С… СѓРїСЂР°Р¶РЅРµРЅРёР№.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
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
            Timber.d("Р’РёР±СЂР°С†РёСЏ РґР»СЏ РґС‹С…Р°РЅРёСЏ РІС‹РїРѕР»РЅРµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РІРёР±СЂР°С†РёРё РґР»СЏ РґС‹С…Р°РЅРёСЏ")
        }
    }

    /**
     * Р’РёР±СЂР°С†РёСЏ РґР»СЏ РјРµРґРёС‚Р°С†РёРё.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
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
            Timber.d("Р’РёР±СЂР°С†РёСЏ РґР»СЏ РјРµРґРёС‚Р°С†РёРё РІС‹РїРѕР»РЅРµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РІРёР±СЂР°С†РёРё РґР»СЏ РјРµРґРёС‚Р°С†РёРё")
        }
    }

    /**
     * Р’РёР±СЂР°С†РёСЏ РґР»СЏ РґРѕСЃС‚РёР¶РµРЅРёСЏ.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
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
            Timber.d("Р’РёР±СЂР°С†РёСЏ РґР»СЏ РґРѕСЃС‚РёР¶РµРЅРёР№ РІС‹РїРѕР»РЅРµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РІРёР±СЂР°С†РёРё РґР»СЏ РґРѕСЃС‚РёР¶РµРЅРёР№")
        }
    }

    /**
     * РћСЃС‚Р°РЅРѕРІРёС‚СЊ РІРёР±СЂР°С†РёСЋ.
     * @param context РљРѕРЅС‚РµРєСЃС‚.
     */
    fun stopVibration(context: Context) {
        val vibrator = getVibrator(context) ?: return

        try {
            vibrator.cancel()
            Timber.d("Р’РёР±СЂР°С†РёСЏ РѕСЃС‚Р°РЅРѕРІР»РµРЅР°")
        } catch (e: Exception) {
            Timber.e(e, "РћС€РёР±РєР° РѕСЃС‚Р°РЅРѕРІРєРё РІРёР±СЂР°С†РёРё")
        }
    }
}
