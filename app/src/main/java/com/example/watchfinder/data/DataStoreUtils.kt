package com.example.watchfinder.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Nombre del archivo de DataStore
private const val PREFERENCES_NAME = "my_app_preferences"

// Crear la instancia de DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)