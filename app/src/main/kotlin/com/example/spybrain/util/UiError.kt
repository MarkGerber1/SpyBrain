package com.example.spybrain.util

// NOTE СЂРµР°Р»РёР·РѕРІР°РЅРѕ РїРѕ Р°СѓРґРёС‚Сѓ: UiError РґР»СЏ РІСЃРµС… UI-СЃРѕСЃС‚РѕСЏРЅРёР№
sealed class UiError {
    object NetworkError : UiError()
    object ValidationError : UiError()
    object UnknownError : UiError()
    data class Custom(val message: String) : UiError()
}
