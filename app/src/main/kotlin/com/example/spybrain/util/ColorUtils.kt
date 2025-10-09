package com.example.spybrain.util

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

/**
 * Утилиты для работы с цветами и контрастом.
 */
object ColorUtils {

    /**
     * Рассчитывает контраст между двумя цветами по формуле WCAG.
     *
     * @param color1 Первый цвет.
     * @param color2 Второй цвет.
     * @return Коэффициент контраста (1.0 - максимальный контраст, 21.0 - минимальный).
     */
    fun calculateContrast(color1: Color, color2: Color): Double {
        val luminance1 = calculateLuminance(color1)
        val luminance2 = calculateLuminance(color2)

        val lighter = maxOf(luminance1, luminance2)
        val darker = minOf(luminance1, luminance2)

        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Рассчитывает яркость цвета по формуле WCAG.
     *
     * @param color Цвет для расчета яркости.
     * @return Яркость цвета (0.0 - черный, 1.0 - белый).
     */
    private fun calculateLuminance(color: Color): Double {
        val red = color.red.toDouble()
        val green = color.green.toDouble()
        val blue = color.blue.toDouble()

        val rsRGB = if (red <= 0.03928) red / 12.92 else Math.pow((red + 0.055) / 1.055, 2.4)
        val gsRGB = if (green <= 0.03928) green / 12.92 else Math.pow((green + 0.055) / 1.055, 2.4)
        val bsRGB = if (blue <= 0.03928) blue / 12.92 else Math.pow((blue + 0.055) / 1.055, 2.4)

        return 0.2126 * rsRGB + 0.7152 * gsRGB + 0.0722 * bsRGB
    }

    /**
     * Возвращает цвет текста с достаточным контрастом для указанного фона.
     * Соответствует стандарту WCAG AA (контраст ≥ 4.5:1 для мелкого текста).
     *
     * @param backgroundColor Цвет фона.
     * @param lightColor Цвет текста для светлого фона (по умолчанию белый).
     * @param darkColor Цвет текста для темного фона (по умолчанию черный).
     * @return Цвет текста с достаточным контрастом.
     */
    fun resolveReadableOn(
        backgroundColor: Color,
        lightColor: Color = Color.White,
        darkColor: Color = Color.Black
    ): Color {
        val contrastWithLight = calculateContrast(backgroundColor, lightColor)
        val contrastWithDark = calculateContrast(backgroundColor, darkColor)

        // Возвращаем цвет с большим контрастом
        return if (contrastWithLight > contrastWithDark) lightColor else darkColor
    }

    /**
     * Создает полупрозрачный слой для улучшения читаемости текста.
     *
     * @param backgroundColor Цвет фона.
     * @param overlayOpacity Прозрачность слоя (0.0 - полностью прозрачный, 1.0 - полностью непрозрачный).
     * @return Цвет слоя для наложения.
     */
    fun createScrimForText(backgroundColor: Color, overlayOpacity: Float = 0.3f): Color {
        val isDarkBackground = calculateLuminance(backgroundColor) < 0.5

        return if (isDarkBackground) {
            Color.Black.copy(alpha = overlayOpacity)
        } else {
            Color.White.copy(alpha = overlayOpacity)
        }
    }

    /**
     * Преобразует цвет в строку в формате hex.
     *
     * @param color Цвет для преобразования.
     * @return Строка в формате #RRGGBB или #AARRGGBB.
     */
    fun colorToHex(color: Color): String {
        val alpha = (color.alpha * 255).roundToInt()
        val red = (color.red * 255).roundToInt()
        val green = (color.green * 255).roundToInt()
        val blue = (color.blue * 255).roundToInt()

        return if (alpha == 255) {
            String.format("#%02X%02X%02X", red, green, blue)
        } else {
            String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
        }
    }

    /**
     * Создает цвет из строки hex.
     *
     * @param hex Строка в формате #RRGGBB или #AARRGGBB.
     * @return Цвет или null если формат неверный.
     */
    fun colorFromHex(hex: String): Color? {
        return try {
            val cleanHex = hex.removePrefix("#")
            when (cleanHex.length) {
                6 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
                8 -> {
                    val alpha = cleanHex.substring(0, 2).toInt(16)
                    val red = cleanHex.substring(2, 4).toInt(16)
                    val green = cleanHex.substring(4, 6).toInt(16)
                    val blue = cleanHex.substring(6, 8).toInt(16)
                    Color(red, green, blue, alpha)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}