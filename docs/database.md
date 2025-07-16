# Database Schema Documentation

## Overview
SpyBrain uses Android Room as an ORM layer on top of the embedded SQLite database that is shipped with every Android device.  Data is stored locally on-device; no PII leaves the user’s handset by default.

* Database name: `spybrain.db`
* Current schema version: **4** (see `AppDatabase.version`)
* ORM: Room 2.x
* Converters: custom `Converters` class (UTC millis ↔ LocalDateTime)

---

## Entity–Relationship (ER) Model
Below is a logical ER diagram of the current schema (v4).  PK – primary key, FK – foreign key.

```
+--------------------+      1         n   +---------------------------+
|  user_profile      |--------------------| breathing_sessions        |
|--------------------|                    |---------------------------|
| PK  id (INT)       |                    | PK id (INT)               |
|     userId (TEXT)  |                +---| FK userId → user_profile  |
|     name (TEXT)    |                |   | patternName (TEXT)        |
|     email (TEXT)   |                |   | durationSeconds (LONG)    |
|     joinDate (LONG)|                |   | timestamp (LONG)          |
|     streakDays(INT)|                |   +---------------------------+
|     avatarUrl(TEXT)|                |   +---------------------------+
                                      |   | meditation_sessions       |
                                      |   |---------------------------|
                                      +---| PK id (INT)               |
                                          | FK userId → user_profile  |
                                          | durationSeconds (LONG)    |
                                          | timestamp (LONG)          |
                                          +---------------------------+

user_profile      1   —— n   heart_rate_measurements
                                          +---------------------------+
                                          | heart_rate_measurements   |
                                          |---------------------------|
                                          | PK id (INT)               |
                                          | FK userId → user_profile  |
                                          | heartRate (INT)           |
                                          | timestamp (TEXT ISO)      |
                                          +---------------------------+

custom_breathing_patterns are *user-generated templates* that can
be referenced from sessions but currently do **not** enforce an FK.

achievements are global, stored per profile but likewise without an FK.
```

> NOTE: Actual FK constraints are not yet declared in Room entities (only implicit in application logic).  See the **TODOs** section for planned hard-FK enforcement.

---

## Table & Column Reference
### 1. `user_profile`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | INTEGER | PK | Autoincrement surrogate key (always 0, single-row table). |
| `userId` | TEXT | NOT NULL | UUID string that ties local data to a backend account. |
| `name` | TEXT | NOT NULL | Display name. |
| `email` | TEXT | NOT NULL | Login / recovery email. |
| `joinDate` | INTEGER | NOT NULL | Epoch millis when the user first launched the app. |
| `streakDays` | INTEGER | NOT NULL | Current meditation streak counter. |
| `avatarUrl` | TEXT | NULLABLE | Remote location of profile avatar. |

### 2. `breathing_sessions`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | INTEGER | PK AUTOINCREMENT |
| `patternName` | TEXT | NOT NULL | Name of the pattern used for this session. |
| `durationSeconds` | INTEGER | NOT NULL | Length of session in seconds. |
| `timestamp` | INTEGER | NOT NULL | Epoch millis when session finished. |

### 3. `meditation_sessions`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | INTEGER | PK AUTOINCREMENT |
| `durationSeconds` | INTEGER | NOT NULL | Length of session in seconds. |
| `timestamp` | INTEGER | NOT NULL | Epoch millis when session finished. |

### 4. `custom_breathing_patterns`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | TEXT | PK | UUID string. |
| `name` | TEXT | NOT NULL | Pattern title shown to the user. |
| `description` | TEXT | NULLABLE | Optional description. |
| `inhaleSeconds` | INTEGER | NOT NULL |
| `holdAfterInhaleSeconds` | INTEGER | NOT NULL |
| `exhaleSeconds` | INTEGER | NOT NULL |
| `holdAfterExhaleSeconds` | INTEGER | NOT NULL |
| `totalCycles` | INTEGER | NOT NULL |

### 5. `achievements`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | TEXT | PK | Unique identifier (e.g. `FIRST_MEDITATION`). |
| `title` | TEXT | NOT NULL | Short name. |
| `description` | TEXT | NOT NULL | Long description. |
| `isUnlocked` | INTEGER | NOT NULL | 0/1 flag. |
| `unlockedAt` | INTEGER | NULLABLE | Epoch millis when unlocked. |

### 6. `heart_rate_measurements`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | INTEGER | PK AUTOINCREMENT |
| `heartRate` | INTEGER | NOT NULL | BPM value. |
| `timestamp` | TEXT | NOT NULL | ISO-8601 date-time string. |

---

## Room Migrations
| From → To | Location | Description |
|-----------|----------|-------------|
| 1 → 2 | `MIGRATION_1_2` | Adds `custom_breathing_patterns` and `user_profile`. |
| 2 → 3 | `MIGRATION_2_3` | Introduces `achievements` table. |
| 3 → 4 | `MIGRATION_3_4` | Adds `heart_rate_measurements` table. |

All migrations are declared in `app/src/main/kotlin/com/example/spybrain/data/storage/Migrations.kt` and are registered when building the Room database instance.

---

## Future Improvements & TODOs
1. **Add Foreign Keys** – enforce referential integrity between `user_profile` and child tables.
2. **Export Schema** – set `exportSchema = true` in `@Database` annotation and retain schema JSON in version control for schema diffing.
3. **Full-Text Search** – consider FTS5 virtual table for fast session notes (post-MVP).
4. **Encryption-At-Rest** – migrate to [SQLCipher for Android](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/) to satisfy privacy/security requirements.
5. **Automatic Back-ups** – enable [RoomAutoBackup](https://developer.android.com/topic/libraries/architecture/room/auto-backup) once minSdk ≥ 31.

---

© 2025 SpyBrain Team
