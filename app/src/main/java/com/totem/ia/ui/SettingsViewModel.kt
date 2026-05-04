package com.totem.ia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.totem.ia.data.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _baseUrl = MutableStateFlow(SettingsManager.DEFAULT_BASE_URL)
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    private val _systemPrompt = MutableStateFlow(SettingsManager.DEFAULT_SYSTEM_PROMPT)
    val systemPrompt: StateFlow<String> = _systemPrompt.asStateFlow()

    init {
        viewModelScope.launch {
            settingsManager.baseUrlFlow.collect { _baseUrl.value = it }
        }
        viewModelScope.launch {
            settingsManager.systemPromptFlow.collect { _systemPrompt.value = it }
        }
    }

    fun updateBaseUrl(url: String) {
        _baseUrl.value = url
        viewModelScope.launch { settingsManager.saveBaseUrl(url) }
    }

    fun updateSystemPrompt(prompt: String) {
        _systemPrompt.value = prompt
        viewModelScope.launch { settingsManager.saveSystemPrompt(prompt) }
    }
}
