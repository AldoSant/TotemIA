package com.totem.ia.ui

import android.speech.tts.Voice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.totem.ia.data.SettingsManager
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
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _baseUrl = MutableStateFlow(SettingsManager.DEFAULT_BASE_URL)
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    private val _systemPrompt = MutableStateFlow(SettingsManager.DEFAULT_SYSTEM_PROMPT)
    val systemPrompt: StateFlow<String> = _systemPrompt.asStateFlow()

    private val _selectedVoiceName = MutableStateFlow("")
    val selectedVoiceName: StateFlow<String> = _selectedVoiceName.asStateFlow()

    private val _availableVoices = MutableStateFlow<List<Voice>>(emptyList())
    val availableVoices: StateFlow<List<Voice>> = _availableVoices.asStateFlow()

    init {
        viewModelScope.launch {
            settingsManager.baseUrlFlow.collect { _baseUrl.value = it }
        }
        viewModelScope.launch {
            settingsManager.systemPromptFlow.collect { _systemPrompt.value = it }
        }
        viewModelScope.launch {
            settingsManager.voiceNameFlow.collect { name ->
                _selectedVoiceName.value = name
                if (name.isNotBlank()) ttsManager.setVoiceByName(name)
            }
        }
        // Load voices with a short delay to let TTS engine initialize
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            _availableVoices.value = ttsManager.getAvailableVoices()
        }
    }

    fun updateBaseUrl(newUrl: String) {
        _baseUrl.value = newUrl
        viewModelScope.launch { settingsManager.saveBaseUrl(newUrl) }
    }

    fun updateSystemPrompt(newPrompt: String) {
        _systemPrompt.value = newPrompt
        viewModelScope.launch { settingsManager.saveSystemPrompt(newPrompt) }
    }

    fun selectVoice(voice: Voice) {
        _selectedVoiceName.value = voice.name
        ttsManager.setVoiceByName(voice.name)
        viewModelScope.launch { settingsManager.saveVoiceName(voice.name) }
    }

    fun previewVoice() {
        ttsManager.speak("Olá! Essa é a voz do Totem IA.")
    }
}
