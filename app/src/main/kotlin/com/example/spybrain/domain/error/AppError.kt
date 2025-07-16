package com.example.spybrain.domain.error

/**
 * sealed class AppError (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
 */
sealed class AppError : Throwable() {
    /**
     * РћС€РёР±РєР° Network (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
     */
    object Network : AppError()
    /**
     * РћС€РёР±РєР° Unknown (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
     */
    object Unknown : AppError()
    /**
     * РљР°СЃС‚РѕРјРЅР°СЏ РѕС€РёР±РєР° (Р°РІС‚РѕСЃРіРµРЅРµСЂРёСЂРѕРІР°РЅРѕ).
     */
    data class Custom(override val message: String) : AppError()
}
