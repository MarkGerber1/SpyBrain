package com.example.spybrain.data.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * РњРёРіСЂР°С†РёСЏ Р±Р°Р·С‹ РґР°РЅРЅС‹С… СЃ РІРµСЂСЃРёРё 1 РЅР° 2. Р”РѕР±Р°РІР»СЏРµС‚ С‚Р°Р±Р»РёС†С‹ custom_breathing_patterns Рё user_profile.
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
 * РњРёРіСЂР°С†РёСЏ Р±Р°Р·С‹ РґР°РЅРЅС‹С… СЃ РІРµСЂСЃРёРё 2 РЅР° 3. Р”РѕР±Р°РІР»СЏРµС‚ С‚Р°Р±Р»РёС†Сѓ achievements.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    /**
     * Р’С‹РїРѕР»РЅСЏРµС‚ РјРёРіСЂР°С†РёСЋ СЃ РІРµСЂСЃРёРё 2 РЅР° 3, СЃРѕР·РґР°РІР°СЏ С‚Р°Р±Р»РёС†Сѓ РґРѕСЃС‚РёР¶РµРЅРёР№.
     * @param database Р­РєР·РµРјРїР»СЏСЂ Р±Р°Р·С‹ РґР°РЅРЅС‹С… РґР»СЏ РІС‹РїРѕР»РЅРµРЅРёСЏ SQL-РєРѕРјР°РЅРґ.
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
 * РњРёРіСЂР°С†РёСЏ Р±Р°Р·С‹ РґР°РЅРЅС‹С… СЃ РІРµСЂСЃРёРё 3 РЅР° 4. Р”РѕР±Р°РІР»СЏРµС‚ С‚Р°Р±Р»РёС†Сѓ heart_rate_measurements.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    /**
     * Р’С‹РїРѕР»РЅСЏРµС‚ РјРёРіСЂР°С†РёСЋ СЃ РІРµСЂСЃРёРё 3 РЅР° 4, СЃРѕР·РґР°РІР°СЏ С‚Р°Р±Р»РёС†Сѓ РёР·РјРµСЂРµРЅРёР№ РїСѓР»СЊСЃР°.
     * @param database Р­РєР·РµРјРїР»СЏСЂ Р±Р°Р·С‹ РґР°РЅРЅС‹С… РґР»СЏ РІС‹РїРѕР»РЅРµРЅРёСЏ SQL-РєРѕРјР°РЅРґ.
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
