package com.totem.ia.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class JourneyWithChapters(
    @Embedded val journey: JourneyEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "journeyId"
    )
    val chapters: List<ChapterEntity>
)
