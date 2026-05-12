package com.totem.ia.data

import android.util.Log
import com.totem.ia.domain.model.Journey
import com.totem.ia.domain.model.UserJourneyState
import javax.inject.Inject

private const val TAG = "JourneyDataSource"

class JourneyDataSource @Inject constructor(
    private val apiService: TotemApiService
) {
    suspend fun getJourneys(baseUrl: String): Result<List<Journey>> = runCatching {
        MockJourneyData.MOCK_JOURNEYS
    }.onFailure { error ->
        Log.e(TAG, "getJourneys failed: ${error.message}", error)
    }

    suspend fun getJourneyDetails(baseUrl: String, journeyId: String): Result<Journey> = runCatching {
        MockJourneyData.MOCK_JOURNEYS.find { it.id == journeyId } 
            ?: throw Exception("Journey not found")
    }.onFailure { error ->
        Log.e(TAG, "getJourneyDetails failed: ${error.message}", error)
    }

    suspend fun getUserJourneyState(baseUrl: String, journeyId: String): Result<UserJourneyState> = runCatching {
        UserJourneyState(journeyId, 0, 0)
    }.onFailure { error ->
        Log.e(TAG, "getUserJourneyState failed: ${error.message}", error)
    }

    suspend fun interactWithJourney(
        baseUrl: String,
        journeyId: String,
        chapterId: String,
        userText: String
    ): Result<String> = runCatching {
        "Resposta simulada do Totem IA para: $userText"
    }.onFailure { error ->
        Log.e(TAG, "interactWithJourney failed: ${error.message}", error)
    }

    suspend fun completeChapter(
        baseUrl: String,
        journeyId: String,
        chapterId: String
    ): Result<UserJourneyState> = runCatching {
        UserJourneyState(journeyId, 1, 10)
    }.onFailure { error ->
        Log.e(TAG, "completeChapter failed: ${error.message}", error)
    }

    private fun buildUrl(baseUrl: String, endpoint: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return "$base$endpoint"
    }
}
