package com.totem.ia.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.totem.ia.audio.SoundscapeManager
import com.totem.ia.bluetooth.TotemBluetoothManager
import com.totem.ia.domain.model.Chapter
import com.totem.ia.domain.model.Journey
import com.totem.ia.domain.repository.JourneyRepository
import com.totem.ia.tts.TextToSpeechManager
import com.totem.ia.voice.SpeechInputManager
import com.totem.ia.voice.VoiceInteractionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EpisodeStage {
    LOADING,
    PLAYING_INTRO, // Reading script
    REFLECTING,   // IA asking questions, user answering
    COMPLETED
}

data class EpisodeUiState(
    val stage: EpisodeStage = EpisodeStage.LOADING,
    val journey: Journey? = null,
    val chapter: Chapter? = null,
    val messages: List<Message> = emptyList(),
    val totemState: TotemState = TotemState.READY,
    val isListening: Boolean = false,
    val rmsLevel: Float = 0f,
    val connectedDeviceName: String? = null
)

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val repository: JourneyRepository,
    private val ttsManager: TextToSpeechManager,
    private val speechManager: SpeechInputManager,
    private val voiceInteractionManager: VoiceInteractionManager,
    private val hapticManager: HapticManager,
    private val bluetoothManager: TotemBluetoothManager,
    private val soundscapeManager: SoundscapeManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val journeyId: String = checkNotNull(savedStateHandle["journeyId"])
    private val chapterId: String = checkNotNull(savedStateHandle["chapterId"])

    private val _stage = MutableStateFlow(EpisodeStage.LOADING)
    private val _journey = MutableStateFlow<Journey?>(null)
    private val _chapter = MutableStateFlow<Chapter?>(null)
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    private val _totemState = MutableStateFlow(TotemState.READY)

    val uiState: StateFlow<EpisodeUiState> = kotlinx.coroutines.flow.combine(
        _stage, _journey, _chapter, _messages, _totemState
    ) { stage, journey, chapter, messages, totemState ->
        EpisodeUiState(stage = stage, journey = journey, chapter = chapter, messages = messages, totemState = totemState)
    }.combine(speechManager.isListening) { state, isListening ->
        state.copy(isListening = isListening)
    }.combine(speechManager.rmsLevel) { state, rmsLevel ->
        state.copy(rmsLevel = rmsLevel)
    }.combine(bluetoothManager.connectedDeviceName) { state, deviceName ->
        state.copy(connectedDeviceName = deviceName)
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = EpisodeUiState()
    )

    init {
        loadData()
        
        speechManager.onResult = { text ->
            if (text.isNotBlank()) {
                handleUserReflection(text)
            }
        }

        ttsManager.onSpeechFinished = {
            soundscapeManager.restoreVolume()
            if (_stage.value == EpisodeStage.PLAYING_INTRO) {
                startReflection()
            } else {
                _totemState.value = TotemState.READY
                voiceInteractionManager.abandonAudioFocus()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getJourneyDetails(journeyId).onSuccess { j ->
                _journey.value = j
                _chapter.value = j.chapters.find { it.id == chapterId }
                _stage.value = EpisodeStage.PLAYING_INTRO
                soundscapeManager.playSoundscape(j.category)
                startEpisode()
            }
        }
    }

    private fun startEpisode() {
        val script = _chapter.value?.scriptBase ?: return
        _totemState.value = TotemState.SPEAKING
        soundscapeManager.duckVolume()
        voiceInteractionManager.requestAudioFocus()
        ttsManager.speak(script)
    }

    private fun startReflection() {
        _stage.value = EpisodeStage.REFLECTING
        val firstPrompt = _chapter.value?.reflectionPrompts?.firstOrNull() ?: "O que você achou dessa reflexão?"
        appendMessage(Message(firstPrompt, isUser = false))
        _totemState.value = TotemState.SPEAKING
        soundscapeManager.duckVolume()
        ttsManager.speak(firstPrompt)
    }

    private fun handleUserReflection(text: String) {
        val limitedText = if (text.length > 2000) text.take(2000) else text
        appendMessage(Message(limitedText, isUser = true))
        _totemState.value = TotemState.THINKING
        
        viewModelScope.launch {
            repository.interactWithJourney(journeyId, chapterId, limitedText)
                .onSuccess { reply ->
                    appendMessage(Message(reply, isUser = false))
                    _totemState.value = TotemState.SPEAKING
                    soundscapeManager.duckVolume()
                    ttsManager.speak(reply)
                }
                .onFailure {
                    _totemState.value = TotemState.READY
                }
        }
    }

    fun toggleListening() {
        if (uiState.value.isListening) {
            speechManager.stopListening()
            soundscapeManager.restoreVolume()
        } else {
            hapticManager.triggerListening()
            _totemState.value = TotemState.LISTENING
            soundscapeManager.duckVolume()
            speechManager.startListening()
        }
    }

    fun finishEpisode() {
        viewModelScope.launch {
            _totemState.value = TotemState.THINKING
            repository.completeChapter(journeyId, chapterId)
                .onSuccess {
                    _stage.value = EpisodeStage.COMPLETED
                    _totemState.value = TotemState.READY
                }
                .onFailure {
                    _totemState.value = TotemState.READY
                }
        }
    }

    private fun appendMessage(message: Message) {
        _messages.value = _messages.value + message
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
        speechManager.stopListening()
        soundscapeManager.stop()
    }
}
