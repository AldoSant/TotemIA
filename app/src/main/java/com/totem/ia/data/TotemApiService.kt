package com.totem.ia.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

data class OpenAiMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class OpenAiRequest(
    @SerializedName("model") val model: String = "local-model",
    @SerializedName("messages") val messages: List<OpenAiMessage>,
    @SerializedName("temperature") val temperature: Double = 0.7
)

data class OpenAiResponse(
    @SerializedName("choices") val choices: List<Choice>
) {
    data class Choice(
        @SerializedName("message") val message: OpenAiMessage
    )
}

interface TotemApiService {
    @POST
    suspend fun sendMessage(@Url url: String, @Body request: OpenAiRequest): OpenAiResponse
}
