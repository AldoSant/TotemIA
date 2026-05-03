package com.totem.ia.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

data class ChatRequest(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("user_text") val userText: String,
    @SerializedName("metadata") val metadata: Map<String, String> = emptyMap()
)

data class ChatResponse(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("reply_text") val replyText: String
)

interface TotemApiService {
    @POST
    suspend fun sendMessage(@Url url: String, @Body request: ChatRequest): ChatResponse
}
