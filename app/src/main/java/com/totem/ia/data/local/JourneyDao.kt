package com.totem.ia.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Transaction
    @Query("SELECT * FROM journeys")
    fun getJourneysWithChapters(): Flow<List<JourneyWithChapters>>

    @Transaction
    @Query("SELECT * FROM journeys WHERE id = :journeyId")
    suspend fun getJourneyWithChaptersById(journeyId: String): JourneyWithChapters?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourneys(journeys: List<JourneyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Transaction
    suspend fun clearAndInsert(journeys: List<JourneyEntity>, chapters: List<ChapterEntity>) {
        deleteAllJourneys()
        insertJourneys(journeys)
        insertChapters(chapters)
    }

    @Query("DELETE FROM journeys")
    suspend fun deleteAllJourneys()
}
