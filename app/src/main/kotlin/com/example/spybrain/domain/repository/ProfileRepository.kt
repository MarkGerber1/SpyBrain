package com.example.spybrain.domain.repository

import com.example.spybrain.domain.model.Profile
import kotlinx.coroutines.flow.Flow

/**
 * РРЅС‚РµСЂС„РµР№СЃ СЂРµРїРѕР·РёС‚РѕСЂРёСЏ РїСЂРѕС„РёР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
 */
interface ProfileRepository {
    /**
     * РџРѕР»СѓС‡РёС‚СЊ РїСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @return РџСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    fun getProfile(): Flow<Profile>

    /**
     * РЎРѕС…СЂР°РЅРёС‚СЊ РїСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     * @param profile РџСЂРѕС„РёР»СЊ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ.
     */
    suspend fun saveProfile(profile: Profile)
}
