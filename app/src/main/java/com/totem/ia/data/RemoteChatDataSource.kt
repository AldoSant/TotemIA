package com.totem.ia.data

import javax.inject.Inject

class RemoteChatDataSource @Inject constructor(
    private val apiService: TotemApiService
) {
    suspend fun sendMessage(baseUrl: String, sessionId: String, text: String, prompt: String): String {
        return try {
            // Se for LM Studio/OpenAI, a rota padrão é /v1/chat/completions ou /chat/completions
            val fullUrl = if (baseUrl.endsWith("/")) "${baseUrl}chat/completions" else "$baseUrl/chat/completions"
            
            val request = OpenAiRequest(
                messages = listOf(
                    OpenAiMessage(role = "system", content = prompt),
                    OpenAiMessage(role = "user", content = text)
                )
            )
            
            val response = apiService.sendMessage(fullUrl, request)
            response.choices.firstOrNull()?.message?.content ?: "Desculpe, não consegui formular uma resposta."
        } catch (e: Exception) {
            e.printStackTrace()
            "Desculpe, ocorreu um erro ao conectar com o Totem."
        }
    }
}
