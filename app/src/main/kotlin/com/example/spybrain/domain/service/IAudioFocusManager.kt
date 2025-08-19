package com.example.spybrain.domain.service

/**
 * Abstraction over Audio Focus to decouple from Android AudioManager in tests.
 */
interface IAudioFocusManager {
    fun requestTransientMayDuck(onLoss: () -> Unit = {}, onGain: () -> Unit = {})
    fun abandon()
}



