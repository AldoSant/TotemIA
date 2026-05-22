package com.totem.ia.data

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path
import com.totem.ia.domain.model.Journey
import com.totem.ia.domain.model.UserJourneyState

// ── Request / Response ────────────────────────────────────────────────────────

data class ChatRequest(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("user_text")  val userText: String,
    @SerializedName("metadata")   val metadata: Map<String, String> = emptyMap()
)

data class ChatResponse(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("reply_text") val replyText: String
)

data class TotemAskRequest(
    @SerializedName("journeyId") val journeyId: String,
    @SerializedName("chapterId") val chapterId: String,
    @SerializedName("userText")  val userText: String,
    @SerializedName("context")   val context: List<String>
)

data class TotemAskResponse(
    @SerializedName("journeyId") val journeyId: String,
    @SerializedName("chapterId") val chapterId: String,
    @SerializedName("answer")    val answer: String,
    @SerializedName("model")     val model: String
)

// ── API Interface ─────────────────────────────────────────────────────────────

interface TotemApiService {

    /** Health check — no API key required (interceptor omits it for /health). */
    @GET("health")
    suspend fun checkHealth(): okhttp3.ResponseBody

    /** Sends a chat message to the Totem AI. */
    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): ChatResponse

    @GET("journeys")
    suspend fun getJourneys(): JsonElement

    @GET("journeys/{journeyId}")
    suspend fun getJourneyDetails(
        @Path("journeyId") journeyId: String
    ): Journey

    @GET("users/{userId}/progress")
    suspend fun getUserJourneyState(
        @Path("userId") userId: String
    ): UserJourneyState

    @POST("totem/ask")
    suspend fun askTotem(
        @Body request: TotemAskRequest
    ): TotemAskResponse

    @POST("journeys/{journeyId}/chapters/{chapterId}/complete")
    suspend fun completeChapter(
        @Path("journeyId") journeyId: String,
        @Path("chapterId") chapterId: String
    ): UserJourneyState
}
