package com.example.spybrain.data.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Миграция базы данных с версии 1 на 2. Добавляет таблицы custom_breathing_patterns и user_profile.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    /**
     * Выполняет миграцию с версии 1 на 2,
     * создавая новые таблицы для паттернов дыхания
     * и профиля пользователя.
     * @param database Экземпляр базы данных для выполнения SQL-команд.
     */
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

/**
 * Миграция базы данных с версии 2 на 3. Добавляет таблицу achievements.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    /**
     * Выполняет миграцию с версии 2 на 3, создавая таблицу достижений.
     * @param database Экземпляр базы данных для выполнения SQL-команд.
     */
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

/**
 * Миграция базы данных с версии 3 на 4. Добавляет таблицу heart_rate_measurements.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    /**
     * Выполняет миграцию с версии 3 на 4, создавая таблицу измерений пульса.
     * @param database Экземпляр базы данных для выполнения SQL-команд.
     */
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `heart_rate_measurements` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `heartRate` INTEGER NOT NULL,
                `timestamp` TEXT NOT NULL
            )
        """.trimIndent())
    }
}

/**
 * Миграция базы данных с версии 4 на 5. Добавляет поля age и gender в user_profile.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Добавляем новые nullable-столбцы, чтобы не нарушить существующие данные
        database.execSQL("ALTER TABLE `user_profile` ADD COLUMN `age` INTEGER")
        database.execSQL("ALTER TABLE `user_profile` ADD COLUMN `gender` TEXT")
    }
}
