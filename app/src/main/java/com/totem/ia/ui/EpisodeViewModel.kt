package com.totem.ia.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.totem.ia.audio.SoundscapeManager
import com.totem.ia.bluetooth.TotemBluetoothManager
import com.totem.ia.data.SettingsManager
import com.totem.ia.domain.model.Chapter
import com.totem.ia.domain.model.Journey
import com.totem.ia.domain.repository.JourneyRepository
import com.totem.ia.text.TextSanitizer
import com.totem.ia.tts.TextToSpeechManager
import com.totem.ia.voice.SpeechInputManager
import com.totem.ia.voice.VoiceInteractionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EpisodeStage {
    LOADING,
    PLAYING_INTRO,
    REFLECTING,
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
    val connectedDeviceName: String? = null,
    val isNarrationPlaying: Boolean = false,
    val isNarrationPaused: Boolean = false,
    val narrationProgressLabel: String = "",
    val sessionStatus: String = "Carregando capítulo...",
    val voiceStatus: String = ""
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
    private val settingsManager: SettingsManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val journeyId: String = checkNotNull(savedStateHandle["journeyId"])
    private val chapterId: String = checkNotNull(savedStateHandle["chapterId"])

    private val _stage = MutableStateFlow(EpisodeStage.LOADING)
    private val _journey = MutableStateFlow<Journey?>(null)
    private val _chapter = MutableStateFlow<Chapter?>(null)
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    private val _totemState = MutableStateFlow(TotemState.READY)
    private val _isNarrationPlaying = MutableStateFlow(false)
    private val _isNarrationPaused = MutableStateFlow(false)
    private val _narrationProgressLabel = MutableStateFlow("")
    private val _sessionStatus = MutableStateFlow("Carregando capítulo...")
    private val _voiceStatus = MutableStateFlow("")

    private var narrationSegments: List<String> = emptyList()
    private var narrationIndex: Int = 0
    private var suppressNextTtsFinished = false

    val uiState: StateFlow<EpisodeUiState> = combine(
        _stage,
        _journey,
        _chapter,
        _messages,
        _totemState,
        _isNarrationPlaying,
        _isNarrationPaused,
        _narrationProgressLabel,
        _sessionStatus,
        _voiceStatus
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        EpisodeUiState(
            stage = values[0] as EpisodeStage,
            journey = values[1] as Journey?,
            chapter = values[2] as Chapter?,
            messages = values[3] as List<Message>,
            totemState = values[4] as TotemState,
            isNarrationPlaying = values[5] as Boolean,
            isNarrationPaused = values[6] as Boolean,
            narrationProgressLabel = values[7] as String,
            sessionStatus = values[8] as String,
            voiceStatus = values[9] as String
        )
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
            _voiceStatus.value = ""
            if (text.isNotBlank()) {
                handleUserReflection(text)
            } else {
                _totemState.value = TotemState.READY
            }
        }

        ttsManager.onSpeechFinished = finish@{
            if (suppressNextTtsFinished) {
                suppressNextTtsFinished = false
                return@finish
            }
            soundscapeManager.restoreVolume()
            if (_stage.value == EpisodeStage.PLAYING_INTRO && _isNarrationPlaying.value) {
                playNextNarrationSegment()
            } else {
                _isNarrationPlaying.value = false
                _totemState.value = TotemState.READY
                _sessionStatus.value = if (_stage.value == EpisodeStage.REFLECTING) {
                    "Pronto para continuar a conversa."
                } else {
                    "Capítulo pronto."
                }
                voiceInteractionManager.abandonAudioFocus()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getJourneyDetails(journeyId).onSuccess { journey ->
                _journey.value = journey
                _chapter.value = journey.chapters.find { it.id == chapterId }
                _stage.value = EpisodeStage.PLAYING_INTRO
                _sessionStatus.value = "Preparando uma experiência guiada..."
                soundscapeManager.playSoundscape(journey.category)
                startEpisode()
            }.onFailure {
                _sessionStatus.value = "Não consegui carregar este capítulo."
            }
        }
    }

    private fun startEpisode() {
        val script = _chapter.value?.scriptBase?.let(TextSanitizer::forDisplay) ?: return
        narrationSegments = splitNarration(script)
        narrationIndex = 0
        _isNarrationPaused.value = false
        _stage.value = EpisodeStage.PLAYING_INTRO
        playCurrentNarrationSegment()
    }

    private fun splitNarration(text: String): List<String> {
        val normalized = text
            .replace("Conteúdo base (bullets):", "")
            .replace("Conteúdo base:", "")
            .split('\n')
            .map { it.trim().trim('-', '•') }
            .filter { it.isNotBlank() }

        return normalized.flatMap { paragraph ->
            paragraph.split(Regex("(?<=[.!?])\\s+"))
                .map { it.trim() }
                .filter { it.length > 12 }
        }.chunked(2).map { it.joinToString(" ") }.ifEmpty { listOf(text) }
    }

    private fun playCurrentNarrationSegment() {
        val segment = narrationSegments.getOrNull(narrationIndex)
        if (segment == null) {
            completeNarrationIntro()
            return
        }
        _isNarrationPlaying.value = true
        _isNarrationPaused.value = false
        _totemState.value = TotemState.SPEAKING
        _sessionStatus.value = "Narrando capítulo"
        _narrationProgressLabel.value = "Trecho ${narrationIndex + 1} de ${narrationSegments.size}"
        soundscapeManager.duckVolume()
        voiceInteractionManager.requestAudioFocus()
        ttsManager.speak(segment)
    }

    private fun playNextNarrationSegment() {
        narrationIndex += 1
        playCurrentNarrationSegment()
    }

    private fun completeNarrationIntro() {
        _isNarrationPlaying.value = false
        _isNarrationPaused.value = false
        _narrationProgressLabel.value = "Introdução concluída"
        soundscapeManager.restoreVolume()
        startReflection()
    }

    fun toggleNarrationPlayback() {
        when {
            _isNarrationPlaying.value -> pauseNarration()
            _stage.value == EpisodeStage.PLAYING_INTRO && _isNarrationPaused.value -> resumeNarration()
            _stage.value == EpisodeStage.PLAYING_INTRO -> resumeNarration()
        }
    }

    private fun pauseNarration() {
        suppressNextTtsFinished = true
        ttsManager.stop()
        soundscapeManager.restoreVolume()
        voiceInteractionManager.abandonAudioFocus()
        _isNarrationPlaying.value = false
        _isNarrationPaused.value = true
        _totemState.value = TotemState.READY
        _sessionStatus.value = "Pausado — retome quando quiser."
    }

    private fun resumeNarration() {
        _sessionStatus.value = "Retomando capítulo..."
        playCurrentNarrationSegment()
    }

    private fun startReflection() {
        _stage.value = EpisodeStage.REFLECTING
        val firstPrompt = _chapter.value?.reflectionPrompts?.firstOrNull()?.let(TextSanitizer::forDisplay)
            ?: "O que mais chamou sua atenção neste capítulo? Pode responder por texto ou pelo microfone, e eu aprofundo a conversa com você."
        appendMessage(Message(firstPrompt, isUser = false))
        _totemState.value = TotemState.SPEAKING
        _sessionStatus.value = "Momento de reflexão"
        soundscapeManager.duckVolume()
        ttsManager.speak(firstPrompt)
    }

    private fun handleUserReflection(text: String) {
        val limitedText = if (text.length > 2000) text.take(2000) else text
        if (_isNarrationPlaying.value) pauseNarration()
        appendMessage(Message(limitedText, isUser = true))
        _totemState.value = TotemState.THINKING
        _sessionStatus.value = "Pensando em uma resposta útil..."

        viewModelScope.launch {
            repository.interactWithJourney(journeyId, chapterId, limitedText)
                .onSuccess { reply ->
                    speakAssistantReply(TextSanitizer.forDisplay(reply))
                }
                .onFailure {
                    speakAssistantReply(buildLocalChapterReply(limitedText))
                }
        }
    }

    private fun speakAssistantReply(reply: String) {
        appendMessage(Message(reply, isUser = false))
        _totemState.value = TotemState.SPEAKING
        _sessionStatus.value = "Respondendo"
        soundscapeManager.duckVolume()
        ttsManager.speak(reply)
    }

    private suspend fun buildLocalChapterReply(userText: String): String {
        val chapter = _chapter.value
        val systemPrompt = settingsManager.systemPromptFlow.first()
        val chapterTitle = chapter?.title ?: "este capítulo"
        val objective = chapter?.objective?.let(TextSanitizer::forDisplay).orEmpty()
        val nextPrompt = chapter?.reflectionPrompts
            ?.firstOrNull { prompt -> _messages.value.none { it.text.contains(prompt.take(30), ignoreCase = true) } }
            ?: "Qual pequeno teste você poderia fazer nas próximas 24 horas para transformar essa percepção em ação?"
        val toneHint = systemPrompt.takeIf { it.isNotBlank() }
            ?: "tom acolhedor, direto e prático"

        return buildString {
            append("Faz sentido. Em *$chapterTitle*, o ponto central é ")
            append(if (objective.isNotBlank()) objective else "transformar essa ideia em uma atitude concreta")
            append(". Pelo que você trouxe — “${userText.take(160)}” — eu olharia para isso sem julgamento e com curiosidade. ")
            append("Minha sugestão prática: escolha uma situação real desta semana e observe onde esse padrão aparece. ")
            append("Para aprofundar: $nextPrompt")
            if (toneHint.isNotBlank()) append("")
        }
    }

    fun sendTextMessage(text: String) {
        val cleanText = text.trim()
        if (cleanText.isBlank() || _stage.value == EpisodeStage.LOADING) return

        if (_stage.value == EpisodeStage.PLAYING_INTRO) {
            pauseNarration()
            _stage.value = EpisodeStage.REFLECTING
        }

        handleUserReflection(cleanText)
    }

    fun toggleListening() {
        if (uiState.value.isListening) {
            speechManager.stopListening()
            soundscapeManager.restoreVolume()
            _voiceStatus.value = "Processando o que foi ouvido..."
        } else {
            if (_isNarrationPlaying.value) pauseNarration()
            hapticManager.triggerListening()
            _totemState.value = TotemState.LISTENING
            _sessionStatus.value = "Ouvindo sua reflexão"
            _voiceStatus.value = "Ouvindo... fale com calma e toque novamente para parar."
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
                    _sessionStatus.value = "Capítulo concluído."
                }
                .onFailure {
                    _totemState.value = TotemState.READY
                    _sessionStatus.value = "Não consegui concluir agora. Tente novamente."
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
