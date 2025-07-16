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
            "sleep", "СЃРѕРЅ", "РІРµС‡РµСЂРЅРёР№-РїРѕРєРѕР№" -> Triple(
                context.getString(R.string.meditation_sleep),
                context.getString(R.string.meditation_sleep_desc),
                context.getString(R.string.category_sleep)
            )
            "morning", "СѓС‚СЂРѕ", "СѓС‚СЂРµРЅРЅСЏСЏ-СЃРІРµР¶РµСЃС‚СЊ" -> Triple(
                context.getString(R.string.meditation_morning),
                context.getString(R.string.meditation_morning_desc),
                context.getString(R.string.category_focus)
            )
            "stress", "СЃС‚СЂРµСЃСЃ", "СЃРЅСЏС‚РёРµ-СЃС‚СЂРµСЃСЃР°" -> Triple(
                context.getString(R.string.meditation_stress_relief),
                context.getString(R.string.meditation_stress_relief_desc),
                context.getString(R.string.category_anxiety)
            )
            "anxiety", "С‚СЂРµРІРѕР¶РЅРѕСЃС‚СЊ", "СЃРїРѕРєРѕР№СЃС‚РІРёРµ-РґСѓС€Рё" -> Triple(
                context.getString(R.string.meditation_anxiety),
                context.getString(R.string.meditation_anxiety_desc),
                context.getString(R.string.category_anxiety)
            )
            "focus", "РєРѕРЅС†РµРЅС‚СЂР°С†РёСЏ", "РєРѕРЅС†РµРЅС‚СЂР°С†РёСЏ-РІРЅРёРјР°РЅРёСЏ" -> Triple(
                context.getString(R.string.meditation_focus),
                context.getString(R.string.meditation_focus_desc),
                context.getString(R.string.category_focus)
            )
            "mindfulness", "РѕСЃРѕР·РЅР°РЅРЅРѕСЃС‚СЊ", "РѕСЃРЅРѕРІС‹-РѕСЃРѕР·РЅР°РЅРЅРѕСЃС‚Рё" -> Triple(
                context.getString(R.string.meditation_mindfulness_basics),
                context.getString(R.string.meditation_mindfulness_basics_desc),
                context.getString(R.string.category_mindfulness)
            )
            "breathing", "РґС‹С…Р°РЅРёРµ", "РіР»СѓР±РѕРєРѕРµ-РґС‹С…Р°РЅРёРµ" -> Triple(
                context.getString(R.string.meditation_deep_breathing),
                context.getString(R.string.meditation_deep_breathing_desc),
                context.getString(R.string.category_focus)
            )
            "body_scan", "СЃРєР°РЅРёСЂРѕРІР°РЅРёРµ-С‚РµР»Р°" -> Triple(
                context.getString(R.string.meditation_body_scan),
                context.getString(R.string.meditation_body_scan_desc),
                context.getString(R.string.category_mindfulness)
            )
            "loving_kindness", "Р»СЋР±СЏС‰Р°СЏ-РґРѕР±СЂРѕС‚Р°" -> Triple(
                context.getString(R.string.meditation_loving_kindness),
                context.getString(R.string.meditation_loving_kindness_desc),
                context.getString(R.string.category_mindfulness)
            )
            "walking", "С…РѕРґСЊР±Р°", "С…РѕРґСЏС‡Р°СЏ-РјРµРґРёС‚Р°С†РёСЏ" -> Triple(
                context.getString(R.string.meditation_walking),
                context.getString(R.string.meditation_walking_desc),
                context.getString(R.string.category_mindfulness)
            )
            "gratitude", "Р±Р»Р°РіРѕРґР°СЂРЅРѕСЃС‚СЊ" -> Triple(
                context.getString(R.string.meditation_gratitude),
                context.getString(R.string.meditation_gratitude_desc),
                context.getString(R.string.category_mindfulness)
            )
            "compassion", "СЃРѕСЃС‚СЂР°РґР°РЅРёРµ" -> Triple(
                context.getString(R.string.meditation_compassion),
                context.getString(R.string.meditation_compassion_desc),
                context.getString(R.string.category_mindfulness)
            )
            "zen", "РґР·РµРЅ" -> Triple(
                context.getString(R.string.meditation_zen),
                context.getString(R.string.meditation_zen_desc),
                context.getString(R.string.category_focus)
            )
            "vipassana", "РІРёРїР°СЃСЃР°РЅР°" -> Triple(
                context.getString(R.string.meditation_vipassana),
                context.getString(R.string.meditation_vipassana_desc),
                context.getString(R.string.category_mindfulness)
            )
            "transcendental", "С‚СЂР°РЅСЃС†РµРЅРґРµРЅС‚Р°Р»СЊРЅР°СЏ" -> Triple(
                context.getString(R.string.meditation_transcendental),
                context.getString(R.string.meditation_transcendental_desc),
                context.getString(R.string.category_focus)
            )
            "evening", "РІРµС‡РµСЂ", "РІРµС‡РµСЂРЅСЏСЏ-РјРµРґРёС‚Р°С†РёСЏ" -> Triple(
                context.getString(R.string.meditation_evening),
                context.getString(R.string.meditation_evening_desc),
                context.getString(R.string.category_sleep)
            )
            else -> {
                // Fallback для неизвестных файлов
                val title = fileName.replace('-', ' ').replace('_', ' ')
                    .split(' ')
                    .joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }
                Triple(title, "РґРёС‚Р°С†РёСЏ РґР»СЏ СЂР°СЃСЃР»Р°Р±Р»РµРЅРёСЏ", "РћР±С‰РёРµ")
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
