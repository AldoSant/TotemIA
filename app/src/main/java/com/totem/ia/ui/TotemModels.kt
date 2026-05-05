package com.totem.ia.ui

import androidx.compose.ui.graphics.Color

enum class TotemState { 
    READY, 
    LISTENING, 
    THINKING, 
    SPEAKING 
}

data class Message(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

private val PurpleNeon  = Color(0xFF8B5CF6)
private val CyanNeon    = Color(0xFF06B6D4)
private val RedNeon     = Color(0xFFEF4444)
private val GreenNeon   = Color(0xFF10B981)

val TotemState.color: Color get() = when (this) {
    TotemState.READY     -> PurpleNeon
    TotemState.LISTENING -> RedNeon
    TotemState.THINKING  -> CyanNeon
    TotemState.SPEAKING  -> GreenNeon
}
