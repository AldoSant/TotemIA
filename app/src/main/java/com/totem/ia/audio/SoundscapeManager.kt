package com.totem.ia.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundscapeManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioDownloader: AudioDownloader
) {
    private var exoPlayer: ExoPlayer? = null
    
    fun playSoundscape(category: String) {
        // Map category to a mock audio URL
        val url = when(category.lowercase()) {
            "filosofia" -> "https://actions.google.com/sounds/v1/water/rain_on_roof.ogg"
            "psicologia" -> "https://actions.google.com/sounds/v1/ambiences/coffee_shop.ogg"
            else -> "https://actions.google.com/sounds/v1/weather/light_wind.ogg"
        }
        val fileName = "${category}_bg.ogg"

        CoroutineScope(Dispatchers.Main).launch {
            val file = audioDownloader.downloadAudioIfNeeded(url, fileName)
            if (file != null) {
                initializePlayer(file.absolutePath)
            }
        }
    }

    private fun initializePlayer(path: String) {
        exoPlayer?.release()
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(path)
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0.4f // Background level
            prepare()
            play()
        }
    }

    fun duckVolume() {
        exoPlayer?.volume = 0.1f
    }

    fun restoreVolume() {
        exoPlayer?.volume = 0.4f
    }

    fun stop() {
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }
}
