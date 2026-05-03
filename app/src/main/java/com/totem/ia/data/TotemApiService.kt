package com.totem.ia.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("user_text") val userText: String,
    @SerializedName("language") val language: String = "pt-BR",
    @SerializedName("metadata") val metadata: Map<String, String> = mapOf("device" to "android")
)

data class ChatResponse(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("reply_text") val replyText: String
)

interface TotemApiService {
    @POST("/chat")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}
