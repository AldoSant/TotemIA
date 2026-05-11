package com.totem.ia.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Url
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

data class JourneyInteractRequest(
    @SerializedName("journey_id") val journeyId: String,
    @SerializedName("chapter_id") val chapterId: String,
    @SerializedName("user_text")  val userText: String
)

// ── API Interface ─────────────────────────────────────────────────────────────

interface TotemApiService {
    /**
     * Sends a chat message to the OpenClaw server.
     */
    @POST
    suspend fun sendMessage(
        @Url  url: String,
        @Body request: ChatRequest
    ): ChatResponse

    @GET
    suspend fun getJourneys(
        @Url url: String
    ): List<Journey>

    @GET
    suspend fun getJourneyDetails(
        @Url url: String
    ): Journey

    @GET
    suspend fun getUserJourneyState(
        @Url url: String
    ): UserJourneyState

    @POST
    suspend fun interactWithJourney(
        @Url url: String,
        @Body request: JourneyInteractRequest
    ): ChatResponse

    @POST
    suspend fun completeChapter(
        @Url url: String
    ): UserJourneyState
}
