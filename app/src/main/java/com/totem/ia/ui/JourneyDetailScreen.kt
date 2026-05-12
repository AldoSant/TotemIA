package com.totem.ia.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.totem.ia.domain.model.Chapter
import com.totem.ia.domain.model.Journey

private val BgDeep = Color(0xFF020205)
private val PurpleNeon = Color(0xFF8B5CF6)
private val CyanNeon = Color(0xFF06B6D4)
private val RedNeon = Color(0xFFEF4444)

@Composable
fun JourneyDetailScreen(
    onBack: () -> Unit,
    onStartChapter: (String, String) -> Unit,
    viewModel: JourneyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        when (val state = uiState) {
            is JourneyDetailUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanNeon)
                }
            }
            is JourneyDetailUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.Red)
                }
            }
            is JourneyDetailUiState.Success -> {
                JourneyDetailContent(state.journey, state.userState, onBack, onStartChapter)
            }
        }
    }
}

@Composable
private fun JourneyDetailContent(
    journey: Journey,
    userState: com.totem.ia.domain.model.UserJourneyState?,
    onBack: () -> Unit,
    onStartChapter: (String, String) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(24.dp).statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    journey.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PurpleNeon,
                    letterSpacing = 4.sp
                )
                Text(
                    journey.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "SOBRE A JORNADA",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanNeon,
                            letterSpacing = 2.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            journey.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(0.8f)
                        )
                    }
                }
            }
            
            if (userState != null && userState.dailyTask != null) {
                item {
                    Surface(
                        color = CyanNeon.copy(0.1f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, CyanNeon.copy(0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "TAREFA DO DIA",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanNeon,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                userState.dailyTask,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (userState != null && userState.progressPercent < 100) {
                item {
                    Button(
                        onClick = { 
                            val nextChapterId = journey.chapters.find { it.order == userState.currentChapter + 1 }?.id 
                                ?: journey.chapters.firstOrNull()?.id ?: ""
                            onStartChapter(journey.id, nextChapterId)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(8.dp))
                        Text("RETOMAR CAPÍTULO ${userState.currentChapter + 1}")
                    }
                }
            }

            item {
                Text(
                    "AVISO DE SEGURANÇA",
                    style = MaterialTheme.typography.labelSmall,
                    color = RedNeon.copy(0.6f),
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Esta jornada não substitui acompanhamento médico ou psicológico. Em caso de crise ou necessidade de ajuda profissional, procure um especialista.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.4f)
                )
            }

            item {
                Text(
                    "CAPÍTULOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanNeon,
                    letterSpacing = 2.sp
                )
            }

            items(journey.chapters) { chapter ->
                val isCompleted = userState != null && chapter.order <= userState.currentChapter
                ChapterItem(chapter, isCompleted) {
                    onStartChapter(journey.id, chapter.id)
                }
            }
        }
    }
}

@Composable
private fun ChapterItem(chapter: Chapter, isCompleted: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = if (isCompleted) Color.White.copy(0.01f) else Color.White.copy(0.03f),
        border = BorderStroke(1.dp, if (isCompleted) Color.Green.copy(0.1f) else Color.White.copy(0.05f))
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .background(
                        if (isCompleted) Color.Green.copy(0.1f) else PurpleNeon.copy(0.1f), 
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.Green, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        chapter.order.toString(),
                        color = PurpleNeon,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCompleted) Color.White.copy(0.4f) else Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${chapter.estimatedDurationMin} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.4f)
                )
            }
            Icon(
                if (isCompleted) Icons.Default.Refresh else Icons.Default.PlayArrow,
                null,
                tint = if (isCompleted) Color.White.copy(0.2f) else CyanNeon,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
