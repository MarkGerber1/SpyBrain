package com.example.spybrain.data.repository

import android.content.Context
import com.example.spybrain.R
import com.example.spybrain.domain.model.Meditation
import com.example.spybrain.domain.model.Session
import com.example.spybrain.domain.repository.MeditationRepository
import com.example.spybrain.data.model.MeditationSessionEntity
import com.example.spybrain.data.model.toEntity
import com.example.spybrain.data.storage.dao.MeditationSessionDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import android.media.MediaMetadataRetriever
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeditationRepositoryImpl @Inject constructor(
    private val dao: MeditationSessionDao,
    @ApplicationContext private val context: Context
) : MeditationRepository {

    override fun getMeditations(): Flow<List<Meditation>> = flow {
        val files = context.assets.list("audio")?.filter { it.endsWith(".mp3") } ?: emptyList()
        val meditations = files.map { fileName ->
            val name = fileName.substringBeforeLast('.')
            // Получаем локализованное название и описание
            val (title, description, category) = getLocalizedMeditationInfo(name)
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
                description = description,
                durationMinutes = durationMin,
                audioUrl = "asset:///audio/$fileName",
                category = category
            )
        }
        emit(meditations)
    }

    private fun getLocalizedMeditationInfo(fileName: String): Triple<String, String, String> {
        return when (fileName.lowercase()) {
            "sleep", "сон", "вечерний-покой" -> Triple(
                context.getString(R.string.meditation_sleep),
                context.getString(R.string.meditation_sleep_desc),
                context.getString(R.string.meditation_category_sleep)
            )
            "morning", "утро", "утренняя-свежесть" -> Triple(
                context.getString(R.string.meditation_morning),
                context.getString(R.string.meditation_morning_desc),
                context.getString(R.string.meditation_category_morning)
            )
            "stress", "стресс", "снятие-стресса" -> Triple(
                context.getString(R.string.meditation_stress),
                context.getString(R.string.meditation_stress_desc),
                context.getString(R.string.meditation_category_stress)
            )
            "anxiety", "тревожность", "спокойствие-души" -> Triple(
                context.getString(R.string.meditation_anxiety),
                context.getString(R.string.meditation_anxiety_desc),
                context.getString(R.string.meditation_category_anxiety)
            )
            "focus", "концентрация", "концентрация-внимания" -> Triple(
                context.getString(R.string.meditation_focus),
                context.getString(R.string.meditation_focus_desc),
                context.getString(R.string.meditation_category_focus)
            )
            "energy", "энергия", "внутренняя-сила" -> Triple(
                context.getString(R.string.meditation_energy),
                context.getString(R.string.meditation_energy_desc),
                context.getString(R.string.meditation_category_energy)
            )
            "creativity", "творчество", "творческое-вдохновение" -> Triple(
                context.getString(R.string.meditation_creativity),
                context.getString(R.string.meditation_creativity_desc),
                context.getString(R.string.meditation_category_creativity)
            )
            "gratitude", "благодарность" -> Triple(
                context.getString(R.string.meditation_gratitude),
                context.getString(R.string.meditation_gratitude_desc),
                context.getString(R.string.meditation_category_gratitude)
            )
            "love", "любовь", "любовь-к-себе" -> Triple(
                context.getString(R.string.meditation_love),
                context.getString(R.string.meditation_love_desc),
                context.getString(R.string.meditation_category_love)
            )
            "healing", "исцеление" -> Triple(
                context.getString(R.string.meditation_healing),
                context.getString(R.string.meditation_healing_desc),
                context.getString(R.string.meditation_category_healing)
            )
            else -> {
                // Fallback для неизвестных файлов
                val title = fileName.replace('-', ' ').replace('_', ' ')
                    .split(' ')
                    .joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }
                Triple(title, "Медитация для расслабления", "Общие")
            }
        }
    }

    override fun getMeditationById(id: String): Flow<Meditation?> =
        getMeditations().map { list -> list.firstOrNull { it.id == id } }

    override suspend fun trackMeditationSession(session: Session) {
        dao.insert(session.toEntity())
    }
} 