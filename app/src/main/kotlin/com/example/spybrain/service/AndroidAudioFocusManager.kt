package com.example.spybrain.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import com.example.spybrain.domain.service.IAudioFocusManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidAudioFocusManager @Inject constructor(
    @ApplicationContext private val context: Context
) : IAudioFocusManager {
    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var focusRequest: AudioFocusRequest? = null

    override fun requestTransientMayDuck(onLoss: () -> Unit, onGain: () -> Unit) {
        if (focusRequest != null) return
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setOnAudioFocusChangeListener { change ->
                when (change) {
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                    AudioManager.AUDIOFOCUS_LOSS -> onLoss()
                    AudioManager.AUDIOFOCUS_GAIN -> onGain()
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> Unit
                }
            }
            .setAudioAttributes(attrs)
            .build()
        val res = audioManager.requestAudioFocus(req)
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            focusRequest = req
        }
    }

    override fun abandon() {
        focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        focusRequest = null
    }
}



