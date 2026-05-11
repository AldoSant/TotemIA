package com.totem.ia.domain.model

import com.google.gson.annotations.SerializedName

data class Journey(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String, // filosofia, psicologia, livro
    @SerializedName("description") val description: String,
    @SerializedName("duration_type") val durationType: String, // dias, capítulos
    @SerializedName("is_recommended") val isRecommended: Boolean = false,
    @SerializedName("is_sensitive") val isSensitive: Boolean = false,
    @SerializedName("chapters") val chapters: List<Chapter> = emptyList()
)

data class Chapter(
    @SerializedName("id") val id: String,
    @SerializedName("journey_id") val journeyId: String,
    @SerializedName("order") val order: Int,
    @SerializedName("title") val title: String,
    @SerializedName("objective") val objective: String = "",
    @SerializedName("script_base") val scriptBase: String,
    @SerializedName("estimated_duration_min") val estimatedDurationMin: Int,
    @SerializedName("reflection_prompts") val reflectionPrompts: List<String> = emptyList()
)

data class UserJourneyState(
    @SerializedName("journey_id") val journeyId: String,
    @SerializedName("current_chapter") val currentChapter: Int,
    @SerializedName("progress_percent") val progressPercent: Int,
    @SerializedName("streak") val streak: Int = 0,
    @SerializedName("daily_task") val dailyTask: String? = null
)

data class UserChapterReflection(
    @SerializedName("journey_id") val journeyId: String,
    @SerializedName("chapter_id") val chapterId: String,
    @SerializedName("answers") val answers: String,
    @SerializedName("ai_notes") val aiNotes: String
)
