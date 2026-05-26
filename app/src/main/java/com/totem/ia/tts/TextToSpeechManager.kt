package com.totem.ia.tts

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import com.totem.ia.text.TextSanitizer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingVoiceName: String? = null
    
    var onSpeechFinished: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setLanguage(Locale("pt", "BR"))
            
            tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onSpeechFinished?.invoke()
                }
                override fun onError(utteranceId: String?) {
                    onSpeechFinished?.invoke()
                }
            })

            isInitialized = true

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts?.setAudioAttributes(audioAttributes)

            pendingVoiceName?.let { applyVoiceByName(it) }
        }
    }

    fun getAvailableVoices(): List<Voice> {
        return tts?.voices
            ?.filter { it.locale.language == "pt" && !it.isNetworkConnectionRequired }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    fun setVoiceByName(name: String) {
        if (isInitialized) {
            applyVoiceByName(name)
        } else {
            pendingVoiceName = name
        }
    }

    private fun applyVoiceByName(name: String) {
        if (name.isBlank()) return
        val voice = tts?.voices?.find { it.name == name }
        voice?.let { tts?.voice = it }
    }

    fun speak(text: String) {
        if (isInitialized && text.isNotBlank()) {
            val cleanText = TextSanitizer.forSpeech(text)
            if (cleanText.isBlank()) return
            val params = Bundle()
            params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, android.media.AudioManager.STREAM_MUSIC)
            // Usar o mesmo ID para o listener capturar o onDone
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "totem_utterance")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
