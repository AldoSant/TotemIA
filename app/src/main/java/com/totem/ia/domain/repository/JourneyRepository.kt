package com.totem.ia.domain.repository

import com.totem.ia.data.JourneyDataSource
import com.totem.ia.data.SettingsManager
import com.totem.ia.domain.model.Journey
import com.totem.ia.domain.model.UserJourneyState
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class JourneyRepository @Inject constructor(
    private val dataSource: JourneyDataSource,
    private val settingsManager: SettingsManager
) {
    suspend fun getJourneys(): Result<List<Journey>> {
        val baseUrl = settingsManager.baseUrlFlow.first()
        return dataSource.getJourneys(baseUrl)
    }

    suspend fun getJourneyDetails(journeyId: String): Result<Journey> {
        val baseUrl = settingsManager.baseUrlFlow.first()
        return dataSource.getJourneyDetails(baseUrl, journeyId)
    }

    suspend fun getUserJourneyState(journeyId: String): Result<UserJourneyState> {
        val baseUrl = settingsManager.baseUrlFlow.first()
        return dataSource.getUserJourneyState(baseUrl, journeyId)
    }

    suspend fun interactWithJourney(
        journeyId: String,
        chapterId: String,
        userText: String
    ): Result<String> {
        val baseUrl = settingsManager.baseUrlFlow.first()
        return dataSource.interactWithJourney(baseUrl, journeyId, chapterId, userText)
    }

    suspend fun completeChapter(
        journeyId: String,
        chapterId: String
    ): Result<UserJourneyState> {
        val baseUrl = settingsManager.baseUrlFlow.first()
        return dataSource.completeChapter(baseUrl, journeyId, chapterId)
    }
}
