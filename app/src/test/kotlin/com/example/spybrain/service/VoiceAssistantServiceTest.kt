package com.example.spybrain.service

import android.content.Context
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertNotNull

class VoiceAssistantServiceTest {
    @Test
    fun `service can be created`() {
        val context = mockk<Context>(relaxed = true)
        val service = VoiceAssistantService(context, null)
        assertNotNull(service)
    }
    // Р”Р»СЏ РЅР°СЃС‚РѕСЏС‰РёС… С‚РµСЃС‚РѕРІ Р»РѕРіРёРєРё РёСЃРїРѕР»СЊР·СѓР№С‚Рµ РёРЅС‚РµРіСЂР°С†РёРѕРЅРЅС‹Рµ С‚РµСЃС‚С‹ СЃ Robolectric РёР»Рё Instrumented
}

