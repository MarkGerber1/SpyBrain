package com.example.spybrain.domain.service

// TODO реализовано: Абстракция для управления плеером
interface IPlayerService {
    fun play(url: String)
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
    fun release()
    fun getCurrentPosition(): Long
    fun getDuration(): Long
    fun seekTo(positionMs: Long)
}
// NOTE реализовано по аудиту: IPlayerService для DI и ViewModel 