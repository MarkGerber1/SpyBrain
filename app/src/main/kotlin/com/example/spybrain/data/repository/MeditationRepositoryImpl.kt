package com.example.spybrain.data.repository

import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.MeditationRepository
import com.example.spybrain.data.model.MeditationSessionEntity
import com.example.spybrain.data.model.toEntity
import com.example.spybrain.data.storage.dao.MeditationSessionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.flow.map

@Singleton
class MeditationRepositoryImpl @Inject constructor(
    private val dao: MeditationSessionDao,
    @ApplicationContext private val context: Context
) : MeditationRepository {

    override fun getMeditations(): Flow<List<Meditation>> = flow {
        val files = context.assets.list("audio")?.filter { it.endsWith(".mp3") } ?: emptyList()
        val meditations = files.map { fileName ->
            val name = fileName.substringBeforeLast('.')
            // Формируем заголовок из имени файла
            val title = name.replace('-', ' ').replace('_', ' ')
                .split(' ')
                .joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }
            // Извлекаем длительность в минутах
            val assetFd = context.assets.openFd("audio/$fileName")
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(assetFd.fileDescriptor, assetFd.startOffset, assetFd.length)
            }
            val durationMs = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
            val durationMin = (durationMs / 1000 / 60).toInt()
            assetFd.close()
            retriever.release()
            Meditation(
                id = name,
                title = title,
                description = "",
                durationMinutes = durationMin,
                audioUrl = "asset:///audio/$fileName",
                category = "Custom"
            )
        }
        emit(meditations)
    }

    override fun getMeditationById(id: String): Flow<Meditation?> =
        getMeditations().map { list -> list.firstOrNull { it.id == id } }

    override suspend fun trackMeditationSession(session: Session) {
        dao.insert(session.toEntity())
    }
} 