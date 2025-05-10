package com.example.spybrain.domain.error

import com.example.spybrain.util.UiError

object ErrorHandler {
    fun handle(error: Throwable): AppError = when (error) {
        is AppError -> error
        else -> AppError.Unknown
    }

    fun mapToUiError(error: AppError): UiError = when (error) {
        AppError.Network -> UiError.NetworkError
        AppError.Unknown -> UiError.UnknownError
        is AppError.Custom -> UiError.Custom(error.message)
    }
} 