package com.totem.ia.domain.repository

import com.totem.ia.data.TotemApiService
import com.totem.ia.data.local.JourneyDao
import com.totem.ia.data.local.ReflectionDao
import com.totem.ia.data.local.ReflectionEntity
import com.totem.ia.data.local.toDomain
import com.totem.ia.data.local.toEntity
import com.totem.ia.data.network.NetworkResult
import com.totem.ia.domain.model.Journey
import com.totem.ia.domain.model.UserJourneyState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class JourneyRepository @Inject constructor(
    private val apiService: TotemApiService,
    private val journeyDao: JourneyDao,
    private val reflectionDao: ReflectionDao
) {
    fun getJourneys(): Flow<NetworkResult<List<Journey>>> = flow {
        emit(NetworkResult.Loading())

        // 1. Emit local cache first
        val localData = journeyDao.getJourneysWithChapters().first()
        if (localData.isNotEmpty()) {
            emit(NetworkResult.Success(localData.map { it.toDomain() }))
        }

        // 2. Fetch from network
        try {
            val response = apiService.getJourneys()
            val remoteJourneys = if (response.isJsonArray) {
                com.google.gson.Gson().fromJson(response, Array<Journey>::class.java).toList()
            } else if (response.isJsonObject) {
                val obj = response.asJsonObject
                val arr = obj.getAsJsonArray("journeys") ?: obj.getAsJsonArray("data") ?: obj.getAsJsonArray("items")
                if (arr != null) {
                    com.google.gson.Gson().fromJson(arr, Array<Journey>::class.java).toList()
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }

            // Update cache
            journeyDao.clearAndInsert(
                journeys = remoteJourneys.map { it.toEntity() },
                chapters = remoteJourneys.flatMap { journey -> journey.chapters.map { it.toEntity() } }
            )
            val updatedLocal = journeyDao.getJourneysWithChapters().first()
            emit(NetworkResult.Success(updatedLocal.map { it.toDomain() }))
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(NetworkResult.Error(e.message ?: "Erro de conexão e sem dados locais"))
            } else {
                // Keep showing local data if network fails
                emit(NetworkResult.Success(localData.map { it.toDomain() }))
            }
        }
    }

    suspend fun getJourneyDetails(journeyId: String): Result<Journey> {
        return try {
            val local = journeyDao.getJourneyWithChaptersById(journeyId)
            if (local != null) {
                Result.success(local.toDomain())
            } else {
                Result.failure(Exception("Jornada não encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserJourneyState(journeyId: String): Result<UserJourneyState> {
        return try {
            val state = apiService.getUserJourneyState("default_user")
            Result.success(state)
        } catch (e: Exception) {
            // Fallback for demo purposes if not connected
            Result.success(UserJourneyState(journeyId, 0, 0))
        }
    }

    suspend fun interactWithJourney(
        journeyId: String,
        chapterId: String,
        userText: String
    ): Result<String> {
        return try {
            val recentReflections = reflectionDao.getRecentReflections(3)
            val contextList = recentReflections.map { "User: ${it.userText}\nTotem: ${it.aiResponse}" }

            val request = com.totem.ia.data.TotemAskRequest(
                journeyId = journeyId,
                chapterId = chapterId,
                userText = userText,
                context = contextList
            )

            val response = apiService.askTotem(request)
            val totemResponse = response.answer

            // Salva a memória no banco local
            reflectionDao.insertReflection(
                ReflectionEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    journeyId = journeyId,
                    chapterId = chapterId,
                    userText = userText,
                    aiResponse = totemResponse,
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(totemResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    suspend fun completeChapter(
        journeyId: String,
        chapterId: String
    ): Result<UserJourneyState> {
        return Result.success(UserJourneyState(journeyId, 1, 10))
    }
}
