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

// ── UI Models ─────────────────────────────────────────────────────────────────

enum class TotemState { READY, LISTENING, THINKING, SPEAKING }

data class Message(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class TotemViewModel @Inject constructor(
    private val askTotemUseCase: AskTotemUseCase,
    private val ttsManager: TextToSpeechManager,
    private val speechManager: SpeechInputManager
) : ViewModel() {

    private val sessionId = UUID.randomUUID().toString()

    private val _totemState = MutableStateFlow(TotemState.READY)
    val totemState: StateFlow<TotemState> = _totemState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    val isListening: StateFlow<Boolean> = speechManager.isListening
    val partialText: StateFlow<String> = speechManager.spokenText
    val rmsLevel: StateFlow<Float> = speechManager.rmsLevel

    init {
        // Quando o reconhecimento termina, envia a mensagem
        speechManager.onResult = { text: String -> 
            if (text.isNotBlank()) sendMessage(text) 
            else _totemState.value = TotemState.READY
        }
        
        // Quando o TTS termina, volta ao estado pronto
        ttsManager.onSpeechFinished = {
            _totemState.value = TotemState.READY
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _totemState.value == TotemState.THINKING) {
            _totemState.value = TotemState.READY
            return
        }

        appendMessage(Message(text = text, isUser = true))
        _totemState.value = TotemState.THINKING

        viewModelScope.launch {
            askTotemUseCase(sessionId, text)
                .onSuccess { reply ->
                    appendMessage(Message(text = reply, isUser = false))
                    _totemState.value = TotemState.SPEAKING
                    ttsManager.speak(reply)
                    // O estado volta para READY via listener onSpeechFinished
                }
                .onFailure { error ->
                    val errorMsg = "CONECTION ERROR: ${error.localizedMessage ?: "Check server status and URL"}"
                    appendMessage(Message(text = errorMsg, isUser = false, isError = true))
                    _totemState.value = TotemState.READY
                }
        }
    }

    fun replayLastResponse() {
        _messages.value.lastOrNull { !it.isUser && !it.isError }?.let { lastBotMsg ->
            _totemState.value = TotemState.SPEAKING
            ttsManager.speak(lastBotMsg.text)
        }
    }

    fun startListening() {
        if (_totemState.value == TotemState.THINKING) return
        _totemState.value = TotemState.LISTENING
        speechManager.startListening()
    }

    fun stopListening() {
        speechManager.stopListening()
    }

    fun toggleListening() {
        if (isListening.value) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun clearHistory() {
        _messages.value = emptyList()
    }

    private fun appendMessage(message: Message) {
        _messages.value = _messages.value + message
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
        ttsManager.shutdown()
    }
}
