package com.example.spybrain.service

import android.content.Context
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertNotNull

class VoiceAssistantServiceTest {
    @Test
    fun `service can be created`() {
        val context = mockk<Context>(relaxed = true)
        val service = VoiceAssistantService(context)
        assertNotNull(service)
    }
    // Для настоящих тестов логики используйте интеграционные тесты с Robolectric или Instrumented
} 