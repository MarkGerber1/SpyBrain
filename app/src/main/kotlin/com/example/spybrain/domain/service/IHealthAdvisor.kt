package com.example.spybrain.domain.service

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЃРѕРІРµС‚РЅРёРєР° РїРѕ Р·РґРѕСЂРѕРІСЊСЋ.
 */
interface IHealthAdvisor {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ СЃРѕРІРµС‚С‹ РїРѕ Р·РґРѕСЂРѕРІСЊСЋ.
     * @param userId РРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РЎРїРёСЃРѕРє СЃРѕРІРµС‚РѕРІ.
     */
    fun getAdvices(userId: String): List<String>
}
// NOTE СЂРµР°Р»РёР·РѕРІР°РЅРѕ РїРѕ Р°СѓРґРёС‚Сѓ: IHealthAdvisor РґР»СЏ DI Рё ViewModel
