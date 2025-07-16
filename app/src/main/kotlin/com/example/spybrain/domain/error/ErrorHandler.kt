package com.example.spybrain.domain.error

import com.example.spybrain.util.UiError

/**
 * РћР±СЂР°Р±РѕС‚С‡РёРє РѕС€РёР±РѕРє РїСЂРёР»РѕР¶РµРЅРёСЏ.
 */
object ErrorHandler {
    /**
     * РџСЂРµРѕР±СЂР°Р·РѕРІР°С‚СЊ Throwable РІ AppError.
     * @param error РћС€РёР±РєР°.
     * @return AppError.
     */
    fun handle(error: Throwable): AppError = when (error) {
        is AppError -> error
        else -> AppError.Unknown
    }

    /**
     * РџСЂРµРѕР±СЂР°Р·РѕРІР°С‚СЊ AppError РІ UiError.
     * @param error AppError.
     * @return UiError.
     */
    fun mapToUiError(error: AppError): UiError = when (error) {
        AppError.Network -> UiError.NetworkError
        AppError.Unknown -> UiError.UnknownError
        is AppError.Custom -> UiError.Custom(error.message)
    }
}
