package com.totem.ia.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "totem_settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val BASE_URL_KEY = stringPreferencesKey("base_url")
        val SYSTEM_PROMPT_KEY = stringPreferencesKey("system_prompt")
        val VOICE_NAME_KEY = stringPreferencesKey("voice_name")
        
        const val DEFAULT_BASE_URL = "http://163.176.220.249:8000"
        const val DEFAULT_SYSTEM_PROMPT = "Você é um totem IA amigável e sábio."
        const val DEFAULT_VOICE_NAME = ""
    }

    val baseUrlFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[BASE_URL_KEY] ?: DEFAULT_BASE_URL
    }

    val systemPromptFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SYSTEM_PROMPT_KEY] ?: DEFAULT_SYSTEM_PROMPT
    }

    val voiceNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[VOICE_NAME_KEY] ?: DEFAULT_VOICE_NAME
    }

    suspend fun saveBaseUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[BASE_URL_KEY] = url
        }
    }

    suspend fun saveSystemPrompt(prompt: String) {
        context.dataStore.edit { preferences ->
            preferences[SYSTEM_PROMPT_KEY] = prompt
        }
    }

    suspend fun saveVoiceName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[VOICE_NAME_KEY] = name
        }
    }
}
