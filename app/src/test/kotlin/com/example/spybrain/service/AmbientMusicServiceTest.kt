package com.example.spybrain.service

import android.content.Context
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertNotNull

class AmbientMusicServiceTest {
    @Test
    fun `service can be created`() {
        val service = AmbientMusicService()
        assertNotNull(service)
    }

    // Для настоящих тестов логики используйте интеграционные тесты с Robolectric или Instrumented
} 