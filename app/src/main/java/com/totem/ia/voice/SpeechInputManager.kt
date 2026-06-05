package com.totem.ia.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechInputManager @Inject constructor(
    @ApplicationContext private val context: Context
) : RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    var onResult: ((String) -> Unit)? = null

    init {
        // Inicialização básica, mas o recognizer real deve ser criado na Main Thread
        // se houver crash, moveremos a criação para o startListening
        setupIntent()
    }

    private fun setupIntent() {
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    private fun ensureRecognizer() {
        if (speechRecognizer == null && SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(this)
        }
    }

    fun startListening() {
        mainHandler.post {
            if (_isListening.value) return@post
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _spokenText.value = "Reconhecimento de voz indisponível neste aparelho."
                return@post
            }
            ensureRecognizer()
            _isListening.value = true
            _spokenText.value = ""
            speechRecognizer?.startListening(speechIntent)
        }
    }

    fun stopListening() {
        mainHandler.post {
            _isListening.value = false
            speechRecognizer?.stopListening()
        }
    }

    fun cancel() {
        mainHandler.post {
            _isListening.value = false
            speechRecognizer?.cancel()
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {
        _rmsLevel.value = rmsdB
    }
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {
        _isListening.value = false
    }

    override fun onError(error: Int) {
        _isListening.value = false
        val message = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "Não entendi, tente novamente."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Silêncio detectado."
            else -> "Erro no reconhecimento ($error)"
        }
        _spokenText.value = message
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val text = matches[0]
            _spokenText.value = text
            onResult?.invoke(text)
        }
        _isListening.value = false
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            _spokenText.value = matches[0]
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    fun destroy() {
        mainHandler.post {
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }
}
