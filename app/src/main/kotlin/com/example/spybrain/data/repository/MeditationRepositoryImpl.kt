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

/**
 * Реализация репозитория медитаций.
 * Предоставляет доступ к медитациям, их трекам и обработку сессий.
 */
@Singleton
class MeditationRepositoryImpl @Inject constructor(
    private val dao: MeditationSessionDao,
    @ApplicationContext private val context: Context
) : MeditationRepository {

    override fun getMeditations(): Flow<List<Meditation>> = flow {
        val pkg = context.packageName
        val androidRes = { name: String -> "android.resource://$pkg/raw/$name" }
        val curated = listOf(
            Meditation(
                id = "angelic",
                title = context.getString(R.string.meditation_angelic_name),
                description = context.getString(R.string.meditation_angelic_about),
                durationMinutes = 3,
                audioUrl = androidRes("meditation_angelic"),
                category = context.getString(R.string.meditation_category_energy)
            ),
            Meditation(
                id = "dreaming",
                title = context.getString(R.string.meditation_dreaming_name),
                description = context.getString(R.string.meditation_dreaming_about),
                durationMinutes = 4,
                audioUrl = androidRes("meditation_dreaming"),
                category = context.getString(R.string.meditation_category_creativity)
            ),
            Meditation(
                id = "forest_spirit",
                title = context.getString(R.string.meditation_forest_name),
                description = context.getString(R.string.meditation_forest_about),
                durationMinutes = 5,
                audioUrl = androidRes("meditation_forest_spirit"),
                category = context.getString(R.string.meditation_category_focus)
            ),
            Meditation(
                id = "night_sky",
                title = context.getString(R.string.meditation_night_sky_name),
                description = context.getString(R.string.meditation_night_sky_about),
                durationMinutes = 4,
                audioUrl = androidRes("meditation_night_sky"),
                category = context.getString(R.string.meditation_category_sleep)
            ),
            Meditation(
                id = "relaxation",
                title = context.getString(R.string.meditation_relaxation_name),
                description = context.getString(R.string.meditation_relaxation_about),
                durationMinutes = 3,
                audioUrl = androidRes("meditation_relaxation"),
                category = context.getString(R.string.category_sleep)
            ),
            Meditation(
                id = "spiritual",
                title = context.getString(R.string.meditation_spiritual_name),
                description = context.getString(R.string.meditation_spiritual_about),
                durationMinutes = 3,
                audioUrl = androidRes("meditation_spiritual"),
                category = context.getString(R.string.meditation_category_morning)
            ),
            Meditation(
                id = "valley_sunset",
                title = context.getString(R.string.meditation_valley_sunset_name),
                description = context.getString(R.string.meditation_valley_sunset_about),
                durationMinutes = 4,
                audioUrl = androidRes("meditation_valley_sunset"),
                category = context.getString(R.string.category_sleep)
            )
        )
        emit(curated)
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
                Triple(title, context.getString(R.string.meditation_track_description), context.getString(R.string.categories))
            }
        }
    }

    override fun getMeditationById(id: String): Flow<Meditation?> =
        getMeditations().map { list -> list.firstOrNull { it.id == id } }

    override suspend fun trackMeditationSession(session: Session) {
        dao.insert(session.toEntity())
    }

    /**
     * Класс трека медитации. Описывает аудиофайл для медитации.
     * @property id Идентификатор трека.
     * @property titleRes Ресурс названия.
     * @property assetPath Путь к файлу.
     */
    data class MeditationTrack(
        /** Идентификатор трека. */
        val id: String,
        /** Ресурс названия. */
        val titleRes: Int,
        /** Путь к файлу. */
        val assetPath: String
    )

    /**
     * Список треков медитации, доступных в приложении.
     */
    val meditationTracks: List<MeditationTrack> = listOf(
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
