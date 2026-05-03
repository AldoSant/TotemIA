package com.totem.ia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.totem.ia.domain.AskTotemUseCase
import com.totem.ia.tts.TextToSpeechManager
import com.totem.ia.voice.SpeechInputManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class TotemState {
    READY, LISTENING, THINKING, SPEAKING
}

data class Message(val text: String, val isUser: Boolean)

@HiltViewModel
class TotemViewModel @Inject constructor(
    private val askTotemUseCase: AskTotemUseCase,
    private val ttsManager: TextToSpeechManager,
    private val speechInputManager: SpeechInputManager
) : ViewModel() {

    val isListening: StateFlow<Boolean> = speechInputManager.isListening

    init {
        speechInputManager.onResult = { text ->
            sendMessage(text)
        }
    }

    private val sessionId = UUID.randomUUID().toString()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _totemState = MutableStateFlow(TotemState.READY)
    val totemState: StateFlow<TotemState> = _totemState.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        _messages.value = _messages.value + Message(text, isUser = true)
        _totemState.value = TotemState.THINKING

        viewModelScope.launch {
            val response = askTotemUseCase(sessionId, text)
            _messages.value = _messages.value + Message(response, isUser = false)
            _totemState.value = TotemState.SPEAKING
            
            ttsManager.speak(response)
            
            // Assume it finishes speaking (could use UtteranceProgressListener for exact timing)
            _totemState.value = TotemState.READY
        }
    }

    fun reolayLastResponse() {
        val lastBotMessage = _messages.value.lastOrNull { !it.isUser }
        lastBotMessage?.let {
            _totemState.value = TotemState.SPEAKING
            ttsManager.speak(it.text)
            _totemState.value = TotemState.READY
        }
    }

    fun startListening() = speechInputManager.startListening()
    fun stopListening() = speechInputManager.stopListening()
}
