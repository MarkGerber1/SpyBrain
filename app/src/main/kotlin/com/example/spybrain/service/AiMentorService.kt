package com.example.spybrain.service

import javax.inject.Inject
import javax.inject.Singleton
import com.example.spybrain.presentation.breathing.BreathingContract
import com.example.spybrain.domain.service.IAiMentor

@Singleton
class AiMentorService @Inject constructor(
    private val voiceAssistantService: VoiceAssistantService
) : IAiMentor {
    private val inhaleAdvices = listOf(
        "РўС‹ РјРѕР»РѕРґРµС†, РїРѕРїСЂРѕР±СѓР№ РІРґРѕС…РЅСѓС‚СЊ С‡СѓС‚СЊ РіР»СѓР±Р¶Рµ.",
        "РЎС„РѕРєСѓСЃРёСЂСѓР№СЃСЏ РЅР° РїРѕР»РѕР¶РµРЅРёРё РґРёР°С„СЂР°РіРјС‹ РїСЂРё РІРґРѕС…Рµ."
    )
    private val holdAfterInhaleAdvices = listOf(
        "РћС‚Р»РёС‡РЅРѕ РґРµСЂР¶РёС€СЊ РїР°СѓР·Сѓ, СЃРѕС…СЂР°РЅСЏР№ СЃРїРѕРєРѕР№СЃС‚РІРёРµ.",
        "РџРѕРїСЂРѕР±СѓР№ РїРѕС‡СѓРІСЃС‚РІРѕРІР°С‚СЊ, РєР°Рє РІРѕР·РґСѓС… Р·Р°РїРѕР»РЅСЏРµС‚ С‚РµР±СЏ."
    )
    private val exhaleAdvices = listOf(
        "РњРµРґР»РµРЅРЅРѕ РІС‹РґРѕС…РЅРё, РѕСЃРІРѕР±РѕР¶РґР°СЏ РІСЃРµ РЅР°РїСЂСЏР¶РµРЅРёРµ.",
        "РЎРѕСЃСЂРµРґРѕС‚РѕС‡СЊСЃСЏ РЅР° СЃРїРѕРєРѕР№РЅРѕРј РІС‹РґРѕС…Рµ."
    )
    private val holdAfterExhaleAdvices = listOf(
        "Р Р°СЃСЃР»Р°Р±СЊСЃСЏ Рё РїРѕР·РІРѕР»СЊ С‚РµР»Сѓ РѕС‚РґРѕС…РЅСѓС‚СЊ.",
        "РџРѕС‡СѓРІСЃС‚РІСѓР№ РІРѕР»РЅС‹ СЃРїРѕРєРѕР№СЃС‚РІРёСЏ РІ С‚РµР»Рµ."
    )
    private val meditationAdvices = listOf(
        "РџРѕС‡СѓРІСЃС‚РІСѓР№ Р·РІСѓРє РјСѓР·С‹РєРё РІРЅСѓС‚СЂРё СЃРµР±СЏ.",
        "РћС‚РїСѓСЃРєР°Р№ РјС‹СЃР»Рё Рё РІРѕР·РІСЂР°С‰Р°Р№СЃСЏ Рє РґС‹С…Р°РЅРёСЋ."
    )

    override fun giveBreathingAdvice(userId: String): String {
        // TODO: Реализовать логику
        return "Совет по дыханию для пользователя $userId"
    }

    override fun giveMeditationAdvice(userId: String): String {
        // TODO: Реализовать логику
        return "Совет по медитации для пользователя $userId"
    }
}
