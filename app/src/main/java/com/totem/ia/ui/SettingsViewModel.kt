package com.totem.ia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.totem.ia.data.SettingsManager
import com.totem.ia.notifications.NotificationScheduler
import com.totem.ia.tts.TextToSpeechManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val notificationScheduler: NotificationScheduler,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _systemPrompt = MutableStateFlow(SettingsManager.DEFAULT_SYSTEM_PROMPT)
    val systemPrompt: StateFlow<String> = _systemPrompt.asStateFlow()

    private val _notificationTime = MutableStateFlow(SettingsManager.DEFAULT_NOTIFICATION_TIME)
    val notificationTime: StateFlow<String> = _notificationTime.asStateFlow()

    init {
        viewModelScope.launch {
            settingsManager.systemPromptFlow.collect { _systemPrompt.value = it }
        }
        viewModelScope.launch {
            settingsManager.notificationTimeFlow.collect { _notificationTime.value = it }
        }
    }

    fun updateSystemPrompt(prompt: String) {
        _systemPrompt.value = prompt
        viewModelScope.launch { settingsManager.saveSystemPrompt(prompt) }
    }

    fun updateNotificationTime(time: String) {
        _notificationTime.value = time
        viewModelScope.launch {
            settingsManager.saveNotificationTime(time)
            notificationScheduler.scheduleDailyReminder()
        }
    }

    fun testVoice() {
        ttsManager.speak("Olá, estou funcionando. Volume e conexão bluetooth estão operacionais.")
    }
}
