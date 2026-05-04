package com.totem.ia.data

import android.util.Log
import javax.inject.Inject

private const val TAG = "RemoteChatDataSource"

/**
 * Handles all network I/O for the chat feature.
 * Returns a [Result] so callers can distinguish between success and failure
 * without relying on sentinel strings.
 */
class RemoteChatDataSource @Inject constructor(
    private val apiService: TotemApiService
) {
    suspend fun sendMessage(
        baseUrl: String,
        sessionId: String,
        userText: String,
        systemPrompt: String
    ): Result<String> = runCatching {
        val url = buildChatUrl(baseUrl)
        val metadata = mapOf(
            "device"        to "android",
            "system_prompt" to systemPrompt
        )
        val request  = ChatRequest(sessionId = sessionId, userText = userText, metadata = metadata)
        val response = apiService.sendMessage(url, request)
        response.replyText
    }.onFailure { error ->
        Log.e(TAG, "sendMessage failed: ${error.message}", error)
    }

    private fun buildChatUrl(baseUrl: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return "${base}chat"
    }
}
