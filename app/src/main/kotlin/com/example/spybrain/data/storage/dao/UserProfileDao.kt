package com.example.spybrain.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spybrain.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє РїСЂРѕС„РёР»СЋ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ РІ Р±Р°Р·Рµ РґР°РЅРЅС‹С….
 */
@Dao
interface UserProfileDao {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РїСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return Flow СЃ СЃСѓС‰РЅРѕСЃС‚СЊСЋ РїСЂРѕС„РёР»СЏ.
     */
    @Query("SELECT * FROM user_profile WHERE id = 0")
    fun getProfile(): Flow<UserProfileEntity?>

    /**
     * Р’СЃС‚Р°РІРёС‚СЊ РёР»Рё РѕР±РЅРѕРІРёС‚СЊ РїСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param entity РЎСѓС‰РЅРѕСЃС‚СЊ РїСЂРѕС„РёР»СЏ.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserProfileEntity)
}
