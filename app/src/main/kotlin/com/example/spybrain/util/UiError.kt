package com.example.spybrain.util

// NOTE реализовано по аудиту: UiError для всех UI-состояний
sealed class UiError {
    object NetworkError : UiError()
    object ValidationError : UiError()
    object UnknownError : UiError()
    data class Custom(val message: String) : UiError()
} 