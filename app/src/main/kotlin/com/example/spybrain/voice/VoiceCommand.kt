package com.example.spybrain.voice

sealed class VoiceCommand {
    object StartBreathing : VoiceCommand()
    object StartMeditation : VoiceCommand()
    object ShowStats : VoiceCommand()
    data class Unknown(val raw: String) : VoiceCommand()
} 