package com.example.spybrain.util

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Тесты для TextSanitizer.
 * Проверяют корректность очистки текста от маркдаун-разметки и специальных символов.
 */
class TextSanitizerTest {

    @Test
    fun `sanitize should remove markdown formatting`() {
        val input = "**Жирный текст** и *курсивный* текст"
        val expected = "Жирный текст и курсивный текст"

        val result = TextSanitizer.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should remove headers markdown`() {
        val input = "# Заголовок\n## Подзаголовок\n### Подподзаголовок"
        val expected = "Заголовок Подзаголовок Подподзаголовок"

        val result = TextSanitizer.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should replace links with text only`() {
        val input = "Текст с [ссылкой](https://example.com) и изображением ![картинка](image.jpg)"
        val expected = "Текст с ссылкой и изображением картинка"

        val result = TextSanitizer.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should remove inline code`() {
        val input = "Код в строке `function()` и блок кода ```kotlin\nfun test() {}\n```"
        val expected = "Код в строке function() и блок кода kotlin fun test() {}"

        val result = TextSanitizer.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should normalize dashes and quotes`() {
        val input = "Текст с — длинным тире и – коротким тире, а также \"кавычками\""
        val expected = "Текст с — длинным тире и - коротким тире, а также \"кавычками\""

        val result = TextSanitizer.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should collapse multiple spaces`() {
        val input = "Текст    с     множественными      пробелами"
        val expected = "Текст с множественными пробелами"

        val result = TextSanitizer.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should trim leading and trailing spaces`() {
        val input = "  Текст с пробелами в начале и конце  "
        val expected = "Текст с пробелами в начале и конце"

        val result = TextSanitizer.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should remove angle brackets and pipes`() {
        val input = "Текст <с> угловыми скобками | и | вертикальными чертами"
        val expected = "Текст с угловыми скобками и вертикальными чертами"

        val result = TextSanitizer.sanitize(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should handle empty and null input`() {
        assertEquals("", TextSanitizer.sanitize(""))
        assertEquals("", TextSanitizer.sanitize(null))
        assertEquals("", TextSanitizer.sanitize("   "))
    }

    @Test
    fun `containsMarkdown should detect markdown formatting`() {
        assertTrue(TextSanitizer.containsMarkdown("**жирный текст**"))
        assertTrue(TextSanitizer.containsMarkdown("# заголовок"))
        assertTrue(TextSanitizer.containsMarkdown("[ссылка](url)"))
        assertTrue(TextSanitizer.containsMarkdown("`код`"))

        assertFalse(TextSanitizer.containsMarkdown("обычный текст"))
        assertFalse(TextSanitizer.containsMarkdown(""))
        assertFalse(TextSanitizer.containsMarkdown(null))
    }

    @Test
    fun `normalizeWhitespace should collapse spaces only`() {
        val input = "Текст    с     множественными      пробелами"
        val expected = "Текст с множественными пробелами"

        val result = TextSanitizer.normalizeWhitespace(input)

        assertEquals(expected, result)
    }

    @Test
    fun `normalizeWhitespace should not remove markdown formatting`() {
        val input = "**Текст    с     маркдауном**"
        val expected = "**Текст с маркдауном**"

        val result = TextSanitizer.normalizeWhitespace(input)

        assertEquals(expected, result)
    }

    @Test
    fun `sanitize should handle complex markdown combinations`() {
        val input = """
            # Главный заголовок

            **Жирный текст** и *курсивный текст*.

            [Ссылка на сайт](https://example.com)

            Код: `val x = 42`

            > Цитата с маркдауном
            > **внутри цитаты**

            ---

            Список:
            - Пункт 1
            - Пункт 2 с **жирным текстом**
        """.trimIndent()

        val result = TextSanitizer.sanitize(input)

        // Проверяем что маркдаун удален, но текст сохранен
        assertFalse(result.contains("**"))
        assertFalse(result.contains("*"))
        assertFalse(result.contains("#"))
        assertFalse(result.contains("["))
        assertFalse(result.contains("]("))
        assertFalse(result.contains("`"))
        assertFalse(result.contains(">"))

        // Проверяем что текст сохранен
        assertTrue(result.contains("Главный заголовок"))
        assertTrue(result.contains("Жирный текст"))
        assertTrue(result.contains("курсивный текст"))
        assertTrue(result.contains("Ссылка на сайт"))
        assertTrue(result.contains("Код: val x = 42"))
        assertTrue(result.contains("Цитата с маркдауном"))
        assertTrue(result.contains("внутри цитаты"))
        assertTrue(result.contains("Список:"))
        assertTrue(result.contains("Пункт 1"))
        assertTrue(result.contains("Пункт 2 с жирным текстом"))
    }
}