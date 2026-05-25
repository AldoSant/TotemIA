package com.totem.ia.data

import android.util.Log
import com.totem.ia.BuildConfig
import javax.inject.Inject

private const val TAG = "RemoteChatDataSource"

/**
 * Handles all network I/O for the chat feature.
 * Returns a [Result] so callers can distinguish between success and failure
 * without relying on sentinel strings.
 *
 * The base URL and API key are handled globally by [com.totem.ia.di.NetworkModule].
 */
class RemoteChatDataSource @Inject constructor(
    private val apiService: TotemApiService
) {
    suspend fun sendMessage(
        sessionId: String,
        userText: String,
        systemPrompt: String
    ): Result<String> = runCatching {
        val metadata = mapOf(
            "device"        to "android",
            "system_prompt" to systemPrompt
        )
        val request  = ChatRequest(sessionId = sessionId, userText = userText, metadata = metadata)
        val response = apiService.sendMessage(request)
        response.replyText
    }.onFailure { error ->
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "sendMessage failed: ${error.message}", error)
        }
    }
}
