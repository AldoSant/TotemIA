package com.totem.ia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = JourneyEntity::class,
            parentColumns = ["id"],
            childColumns = ["journeyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("journeyId")]
)
data class ChapterEntity(
    @PrimaryKey val id: String,
    val journeyId: String,
    val order: Int,
    val title: String,
    val objective: String,
    val scriptBase: String,
    val estimatedDurationMin: Int,
    val reflectionPrompts: List<String>
)
