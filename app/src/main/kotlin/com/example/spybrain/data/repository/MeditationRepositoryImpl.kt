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
                context.getString(R.string.category_sleep)
            )
            "morning", "утро", "утренняя-свежесть" -> Triple(
                context.getString(R.string.meditation_morning),
                context.getString(R.string.meditation_morning_desc),
                context.getString(R.string.category_focus)
            )
            "stress", "стресс", "снятие-стресса" -> Triple(
                context.getString(R.string.meditation_stress_relief),
                context.getString(R.string.meditation_stress_relief_desc),
                context.getString(R.string.category_anxiety)
            )
            "anxiety", "тревожность", "спокойствие-души" -> Triple(
                context.getString(R.string.meditation_anxiety),
                context.getString(R.string.meditation_anxiety_desc),
                context.getString(R.string.category_anxiety)
            )
            "focus", "концентрация", "концентрация-внимания" -> Triple(
                context.getString(R.string.meditation_focus),
                context.getString(R.string.meditation_focus_desc),
                context.getString(R.string.category_focus)
            )
            "mindfulness", "осознанность", "основы-осознанности" -> Triple(
                context.getString(R.string.meditation_mindfulness_basics),
                context.getString(R.string.meditation_mindfulness_basics_desc),
                context.getString(R.string.category_mindfulness)
            )
            "breathing", "дыхание", "глубокое-дыхание" -> Triple(
                context.getString(R.string.meditation_deep_breathing),
                context.getString(R.string.meditation_deep_breathing_desc),
                context.getString(R.string.category_focus)
            )
            "body_scan", "сканирование-тела" -> Triple(
                context.getString(R.string.meditation_body_scan),
                context.getString(R.string.meditation_body_scan_desc),
                context.getString(R.string.category_mindfulness)
            )
            "loving_kindness", "любящая-доброта" -> Triple(
                context.getString(R.string.meditation_loving_kindness),
                context.getString(R.string.meditation_loving_kindness_desc),
                context.getString(R.string.category_mindfulness)
            )
            "walking", "ходьба", "ходячая-медитация" -> Triple(
                context.getString(R.string.meditation_walking),
                context.getString(R.string.meditation_walking_desc),
                context.getString(R.string.category_mindfulness)
            )
            "gratitude", "благодарность" -> Triple(
                context.getString(R.string.meditation_gratitude),
                context.getString(R.string.meditation_gratitude_desc),
                context.getString(R.string.category_mindfulness)
            )
            "compassion", "сострадание" -> Triple(
                context.getString(R.string.meditation_compassion),
                context.getString(R.string.meditation_compassion_desc),
                context.getString(R.string.category_mindfulness)
            )
            "zen", "дзен" -> Triple(
                context.getString(R.string.meditation_zen),
                context.getString(R.string.meditation_zen_desc),
                context.getString(R.string.category_focus)
            )
            "vipassana", "випассана" -> Triple(
                context.getString(R.string.meditation_vipassana),
                context.getString(R.string.meditation_vipassana_desc),
                context.getString(R.string.category_mindfulness)
            )
            "transcendental", "трансцендентальная" -> Triple(
                context.getString(R.string.meditation_transcendental),
                context.getString(R.string.meditation_transcendental_desc),
                context.getString(R.string.category_focus)
            )
            "evening", "вечер", "вечерняя-медитация" -> Triple(
                context.getString(R.string.meditation_evening),
                context.getString(R.string.meditation_evening_desc),
                context.getString(R.string.category_sleep)
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

    data class MeditationTrack(
        val id: String,
        val titleRes: Int,
        val assetPath: String
    )

    val meditationTracks = listOf(
        MeditationTrack("angelic", R.string.meditation_track_angelic, "audio/meditation_music/meditation_angelic.mp3"),
        MeditationTrack("chill", R.string.meditation_track_chill, "audio/meditation_music/meditation_chill.mp3"),
        MeditationTrack("dreaming", R.string.meditation_track_dreaming, "audio/meditation_music/meditation_dreaming.mp3"),
        MeditationTrack("forest_spirit", R.string.meditation_track_forest_spirit, "audio/meditation_music/meditation_forest_spirit.mp3"),
        MeditationTrack("night_sky", R.string.meditation_track_night_sky, "audio/meditation_music/meditation_night_sky.mp3"),
        MeditationTrack("relaxation", R.string.meditation_track_relaxation, "audio/meditation_music/meditation_relaxation.mp3"),
        MeditationTrack("spiritual", R.string.meditation_track_spiritual, "audio/meditation_music/meditation_spiritual.mp3"),
        MeditationTrack("valley_sunset", R.string.meditation_track_valley_sunset, "audio/meditation_music/meditation_valley_sunset.mp3")
    )
} 