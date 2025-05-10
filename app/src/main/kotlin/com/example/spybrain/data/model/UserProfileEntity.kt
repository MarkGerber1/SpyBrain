package com.example.spybrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spybrain.domain.model.Profile
import java.util.Date

@Entity(tableName = "user_profile")
 data class UserProfileEntity(
     @PrimaryKey val id: Int = 0,
     val userId: String,
     val name: String,
     val email: String,
     val joinDate: Long,
     val streakDays: Int,
     val avatarUrl: String?
 )

fun UserProfileEntity.toDomain(): Profile = Profile(
    userId = userId,
    name = name,
    email = email,
    joinDate = Date(joinDate),
    streakDays = streakDays,
    avatarUrl = avatarUrl
)

fun Profile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = 0,
    userId = userId,
    name = name,
    email = email,
    joinDate = joinDate.time,
    streakDays = streakDays,
    avatarUrl = avatarUrl
) 