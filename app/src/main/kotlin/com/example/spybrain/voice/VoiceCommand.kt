package com.example.spybrain.voice

/**
 * sealed class VoiceCommand (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ)
 */
sealed class VoiceCommand {
    /**
     * РљРѕРјР°РЅРґР° StartBreathing (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
     */
    object StartBreathing : VoiceCommand()
    /**
     * РљРѕРјР°РЅРґР° StartMeditation (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
     */
    object StartMeditation : VoiceCommand()
    /**
     * РљРѕРјР°РЅРґР° ShowStats (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
     */
    object ShowStats : VoiceCommand()
    /**
     * РљРѕРјР°РЅРґР° Unknown (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
     */
    data class Unknown(val raw: String) : VoiceCommand()
}
