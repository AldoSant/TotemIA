package com.totem.ia.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: ReflectionEntity)

    // Gets all reflections ordered by time (for the Diary Screen)
    @Query("SELECT * FROM reflections ORDER BY timestamp DESC")
    fun getAllReflections(): Flow<List<ReflectionEntity>>

    // Gets the most recent reflections to inject as RAG context for the LLM
    @Query("SELECT * FROM reflections ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentReflections(limit: Int = 3): List<ReflectionEntity>

}
