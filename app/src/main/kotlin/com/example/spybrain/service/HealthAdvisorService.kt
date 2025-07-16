package com.example.spybrain.service

import com.example.spybrain.domain.service.IHealthAdvisor
import com.example.spybrain.presentation.breathing.BreathingContract
import javax.inject.Inject
import kotlin.random.Random

/**
 * РЎРµСЂРІРёСЃ РґР»СЏ РІС‹РґР°С‡Рё РґРёРЅР°РјРёС‡РµСЃРєРёС… СЃРѕРІРµС‚РѕРІ РІ РїСЂРѕС†РµСЃСЃРµ РґС‹С…Р°С‚РµР»СЊРЅС‹С… РїСЂР°РєС‚РёРє
 */
class HealthAdvisorService @Inject constructor() : IHealthAdvisor {
    private val inhaleTips = listOf(
        "РЎРєРѕРЅС†РµРЅС‚СЂРёСЂСѓР№С‚РµСЃСЊ РЅР° РјРµРґР»РµРЅРЅРѕРј РІРґРѕС…Рµ",
        "РџРѕРїСЂРѕР±СѓР№С‚Рµ РіР»СѓР±Р¶Рµ РІРґРѕС…РЅСѓС‚СЊ С‡РµСЂРµР· РЅРѕСЃ"
    )
    private val holdAfterInhaleTips = listOf(
        "Р”РµСЂР¶РёС‚Рµ РІРѕР·РґСѓС…: С‡СѓРІСЃС‚РІСѓРµС‚Рµ СЂР°СЃСЃР»Р°Р±Р»РµРЅРёРµ?",
        "РЎРѕС…СЂР°РЅСЏР№С‚Рµ СЃРїРѕРєРѕР№СЃС‚РІРёРµ РЅР° РїР°СѓР·Рµ"
    )
    private val exhaleTips = listOf(
        "Р’С‹РґРѕС…РЅРёС‚Рµ РјРµРґР»РµРЅРЅРѕ, РѕСЃРІРѕР±РѕР¶РґР°СЏ РЅР°РїСЂСЏР¶РµРЅРёРµ",
        "РћС‚РїСѓСЃС‚РёС‚Рµ РІРѕР·РґСѓС… РїР»Р°РІРЅРѕ"
    )
    private val holdAfterExhaleTips = listOf(
        "РџРѕС‡СѓРІСЃС‚РІСѓР№С‚Рµ Р»С‘РіРєРѕСЃС‚СЊ РїРѕСЃР»Рµ РІС‹РґРѕС…Р°",
        "Р Р°СЃСЃР»Р°Р±СЊС‚РµСЃСЊ РїРµСЂРµРґ СЃР»РµРґСѓСЋС‰РёРј РІРґРѕС…РѕРј"
    )

    /**
     * Р¤РѕСЂРјРёСЂСѓРµС‚ СЃРїРёСЃРѕРє СЃРѕРІРµС‚РѕРІ РІ Р·Р°РІРёСЃРёРјРѕСЃС‚Рё РѕС‚ С„Р°Р·С‹ РґС‹С…Р°РЅРёСЏ Рё С‚РµРєСѓС‰РµРіРѕ BPM
     */
    override fun getAdvices(userId: String): List<String> {
        // TODO: Реализовать логику
        // Временно: throw NotImplementedError("HealthAdvisorService logic not implemented yet")
        return listOf("Совет по здоровью для пользователя $userId")
    }
}
// NOTE СЂРµР°Р»РёР·РѕРІР°РЅРѕ РїРѕ Р°СѓРґРёС‚Сѓ: IHealthAdvisor Р°РґР°РїС‚РµСЂ
