package com.example.spybrain.data.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Migration from version 1 to 2: add custom patterns and user profile tables
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `custom_breathing_patterns` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `name` TEXT NOT NULL,
                `description` TEXT,
                `inhaleSeconds` INTEGER NOT NULL,
                `holdAfterInhaleSeconds` INTEGER NOT NULL,
                `exhaleSeconds` INTEGER NOT NULL,
                `holdAfterExhaleSeconds` INTEGER NOT NULL,
                `totalCycles` INTEGER NOT NULL
            )
        """.trimIndent())
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `user_profile` (
                `id` INTEGER NOT NULL PRIMARY KEY,
                `userId` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `email` TEXT NOT NULL,
                `joinDate` INTEGER NOT NULL,
                `streakDays` INTEGER NOT NULL,
                `avatarUrl` TEXT
            )
        """.trimIndent())
    }
}

// Migration from version 2 to 3: add achievements table
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `achievements` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `title` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `isUnlocked` INTEGER NOT NULL,
                `unlockedAt` INTEGER
            )
        """.trimIndent())
    }
} 