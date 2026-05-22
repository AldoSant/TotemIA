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
        val SYSTEM_PROMPT_KEY = stringPreferencesKey("system_prompt")
        val VOICE_NAME_KEY = stringPreferencesKey("voice_name")
        val HAS_SEEN_ONBOARDING_KEY = stringPreferencesKey("has_seen_onboarding")
        val NOTIFICATION_TIME_KEY = stringPreferencesKey("notification_time")

        const val DEFAULT_SYSTEM_PROMPT = "Você é um totem de inteligência artificial amigável. Responda de forma concisa e útil."
        const val DEFAULT_VOICE_NAME = ""
        const val DEFAULT_NOTIFICATION_TIME = "07:00"
    }

    val systemPromptFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SYSTEM_PROMPT_KEY] ?: DEFAULT_SYSTEM_PROMPT
    }

    val voiceNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[VOICE_NAME_KEY] ?: DEFAULT_VOICE_NAME
    }

    val hasSeenOnboardingFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[HAS_SEEN_ONBOARDING_KEY]?.toBoolean() ?: false
    }

    val notificationTimeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATION_TIME_KEY] ?: DEFAULT_NOTIFICATION_TIME
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

    suspend fun saveHasSeenOnboarding(hasSeen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_SEEN_ONBOARDING_KEY] = hasSeen.toString()
        }
    }

    suspend fun saveNotificationTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_TIME_KEY] = time
        }
    }
}
