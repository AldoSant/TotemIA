package com.totem.ia.data.local

import com.totem.ia.domain.model.Journey
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test
    fun `journey toEntity and toDomain should map correctly`() {
        val journey = Journey(
            id = "test-1",
            title = "Title",
            category = "Cat",
            description = "Desc",
            durationType = "days",
            isRecommended = true,
            isSensitive = false,
            chapters = emptyList()
        )
        
        val entity = journey.toEntity()
        assertEquals("test-1", entity.id)
        assertEquals("Title", entity.title)
        
        val journeyWithChapters = JourneyWithChapters(entity, emptyList())
        val mappedJourney = journeyWithChapters.toDomain()
        
        assertEquals(journey.id, mappedJourney.id)
        assertEquals(journey.title, mappedJourney.title)
    }
}
