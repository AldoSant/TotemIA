package com.totem.ia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "reflections",
    indices = [Index("journeyId"), Index("chapterId")]
)
data class ReflectionEntity(
    @PrimaryKey val id: String,
    val journeyId: String,
    val chapterId: String,
    val userText: String,
    val aiResponse: String,
    val timestamp: Long
)
