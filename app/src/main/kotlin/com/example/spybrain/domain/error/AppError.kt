package com.example.spybrain.domain.error

sealed class AppError : Throwable() {
    object Network : AppError()
    object Unknown : AppError()
    data class Custom(override val message: String) : AppError()
} 