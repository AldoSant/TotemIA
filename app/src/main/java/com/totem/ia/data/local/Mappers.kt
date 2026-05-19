package com.totem.ia.data.local

import com.totem.ia.domain.model.Chapter
import com.totem.ia.domain.model.Journey

fun JourneyWithChapters.toDomain(): Journey {
    return Journey(
        id = this.journey.id,
        title = this.journey.title,
        category = this.journey.category,
        description = this.journey.description,
        durationType = this.journey.durationType,
        isRecommended = this.journey.isRecommended,
        isSensitive = this.journey.isSensitive,
        chapters = this.chapters.map { it.toDomain() }.sortedBy { it.order }
    )
}

fun ChapterEntity.toDomain(): Chapter {
    return Chapter(
        id = this.id,
        journeyId = this.journeyId,
        order = this.order,
        title = this.title,
        objective = this.objective,
        scriptBase = this.scriptBase,
        estimatedDurationMin = this.estimatedDurationMin,
        reflectionPrompts = this.reflectionPrompts
    )
}

fun Journey.toEntity(): JourneyEntity {
    return JourneyEntity(
        id = this.id,
        title = this.title,
        category = this.category,
        description = this.description,
        durationType = this.durationType,
        isRecommended = this.isRecommended,
        isSensitive = this.isSensitive
    )
}

fun Chapter.toEntity(): ChapterEntity {
    return ChapterEntity(
        id = this.id,
        journeyId = this.journeyId,
        order = this.order,
        title = this.title,
        objective = this.objective,
        scriptBase = this.scriptBase,
        estimatedDurationMin = this.estimatedDurationMin,
        reflectionPrompts = this.reflectionPrompts
    )
}
