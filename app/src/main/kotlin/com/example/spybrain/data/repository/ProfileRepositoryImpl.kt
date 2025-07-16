package com.example.spybrain.data.repository

import com.example.spybrain.domain.model.Profile
import com.example.spybrain.domain.repository.ProfileRepository
import com.example.spybrain.data.storage.dao.UserProfileDao
import com.example.spybrain.data.model.toDomain
import com.example.spybrain.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория профиля пользователя.
 * Сохраняет и предоставляет профиль через DAO.
 */
@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : ProfileRepository {

    override fun getProfile(): Flow<Profile> =
        dao.getProfile()
            .map { entity ->
                entity?.toDomain() ?: Profile(
                    userId = "guest",
                    name = "Guest",
                    email = "",
                    joinDate = Date(),
                    streakDays = 0,
                    avatarUrl = null
                )
            }

    override suspend fun saveProfile(profile: Profile) {
        dao.upsert(profile.toEntity())
    }
}
