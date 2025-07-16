package com.example.spybrain.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Р­РєР·РµРјРїР»СЏСЂ DataStore РґР»СЏ С…СЂР°РЅРµРЅРёСЏ РЅР°СЃС‚СЂРѕРµРє.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
