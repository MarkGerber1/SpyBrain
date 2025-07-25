﻿package com.example.spybrain.data.repository

import android.content.Context
import com.example.spybrain.domain.model.MeditationProgram
import com.example.spybrain.domain.repository.MeditationProgramRepository
import com.example.spybrain.R
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория программ медитации.
  Предоставляет статический список программ.
 */
@Singleton
class MeditationProgramRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MeditationProgramRepository {
    override fun getMeditationPrograms(): Flow<List<MeditationProgram>> {
        // РЎС‚Р°С‚РёС‡РµСЃРєРёРµ РїСЂРѕРіСЂР°РјРјС‹ РїРѕРєР° РѕС‚СЃСўСЃС‚РІСѓСЋС‚
        return flowOf(emptyList())
    }
}
