package com.example.spybrain.domain.service

/**
 * РРЅС‚РµСЂС„РµР№СЃ AI-РјРµРЅС‚РѕСЂР° РґР»СЏ СЃРѕРІРµС‚РѕРІ РїРѕ РјРµРґРёС‚Р°С†РёРё Рё РґС‹С…Р°РЅРёСЋ.
 */
interface IAiMentor {
    /**
     * Р”Р°С‚СЊ СЃРѕРІРµС‚ РїРѕ РјРµРґРёС‚Р°С†РёРё.
     * @param userId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РЎРѕРІРµС‚ РїРѕ РјРµРґРёС‚Р°С†РёРё.
     */
    fun giveMeditationAdvice(userId: String): String

    /**
     * Р”Р°С‚СЊ СЃРѕРІРµС‚ РїРѕ РґС‹С…Р°РЅРёСЋ.
     * @param userId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РЎРѕРІРµС‚ РїРѕ РґС‹С…Р°С‚РµР»СЊРЅС‹Рј РїСЂР°РєС‚РёРєР°Рј.
     */
    fun giveBreathingAdvice(userId: String): String
}
// NOTE СЂРµР°Р»РёР·РѕРІР°РЅРѕ РїРѕ Р°СѓРґРёС‚Сѓ: IAiMentor РґР»СЏ DI Рё ViewModel
