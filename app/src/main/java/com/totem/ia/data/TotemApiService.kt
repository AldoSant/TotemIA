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
    ): com.google.gson.JsonElement

    @GET
    suspend fun getJourneyDetails(
        @Url url: String
    ): Journey

    @GET
    suspend fun getUserJourneyState(
        @Url url: String
    ): UserJourneyState

    @POST
    suspend fun askTotem(
        @Url url: String,
        @Body request: TotemAskRequest
    ): TotemAskResponse

    @POST
    suspend fun completeChapter(
        @Url url: String
    ): UserJourneyState
}
