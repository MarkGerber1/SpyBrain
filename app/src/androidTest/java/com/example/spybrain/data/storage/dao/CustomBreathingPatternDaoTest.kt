package com.example.spybrain.data.storage.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spybrain.data.model.CustomBreathingPatternEntity
import com.example.spybrain.data.storage.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomBreathingPatternDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: CustomBreathingPatternDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.customBreathingPatternDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndGetPatterns() = runBlocking {
        val pattern = CustomBreathingPatternEntity(
            id = "id1",
            name = "Test Pattern",
            description = "Desc",
            inhaleSeconds = 4,
            holdAfterInhaleSeconds = 2,
            exhaleSeconds = 6,
            holdAfterExhaleSeconds = 2,
            totalCycles = 5
        )
        dao.insertPattern(pattern)
        val list = dao.getAllPatterns().first()
        Assert.assertEquals(1, list.size)
        Assert.assertEquals("id1", list[0].id)
    }

    @Test
    fun deletePattern() = runBlocking {
        val pattern = CustomBreathingPatternEntity(
            id = "id2",
            name = "Delete Pattern",
            description = null,
            inhaleSeconds = 3,
            holdAfterInhaleSeconds = 1,
            exhaleSeconds = 5,
            holdAfterExhaleSeconds = 1,
            totalCycles = 4
        )
        dao.insertPattern(pattern)
        dao.deletePattern(pattern)
        val list = dao.getAllPatterns().first()
        Assert.assertTrue(list.isEmpty())
    }
}
