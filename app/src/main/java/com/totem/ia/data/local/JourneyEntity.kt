package com.totem.ia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journeys")
data class JourneyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val description: String,
    val durationType: String,
    val isRecommended: Boolean,
    val isSensitive: Boolean
)
