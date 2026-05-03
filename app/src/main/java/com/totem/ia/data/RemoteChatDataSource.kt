package com.totem.ia.data

import javax.inject.Inject

class RemoteChatDataSource @Inject constructor(
    private val apiService: TotemApiService
) {
    suspend fun sendMessage(baseUrl: String, sessionId: String, text: String, prompt: String): String {
        return try {
            val fullUrl = if (baseUrl.endsWith("/")) "${baseUrl}chat" else "$baseUrl/chat"
            val metadata = mapOf("device" to "android", "system_prompt" to prompt)
            val request = ChatRequest(sessionId = sessionId, userText = text, metadata = metadata)
            
            val response = apiService.sendMessage(fullUrl, request)
            response.replyText
        } catch (e: Exception) {
            e.printStackTrace()
            "Desculpe, ocorreu um erro ao conectar com o Totem."
        }
    }
}
