package com.totem.ia.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import com.totem.ia.domain.model.Journey

private val BgDeep = Color(0xFF020205)
private val PurpleNeon = Color(0xFF8B5CF6)
private val CyanNeon = Color(0xFF06B6D4)

@Composable
fun JourneyListScreen(
    onJourneyClick: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProgress: () -> Unit,
    viewModel: JourneyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(Modifier.fillMaxSize()) {
            // Header
            Row(
                Modifier.fillMaxWidth().padding(24.dp).statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (uiState !is JourneyUiState.Error) Color(0xFF10B981) else Color.Red)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "JORNADAS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleNeon,
                            letterSpacing = 4.sp
                        )
                    }
                    Text(
                        "Desenvolvimento",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
                Row {
                    IconButton(onClick = onNavigateToProgress) {
                        Icon(Icons.Default.BarChart, null, tint = Color.White.copy(0.4f))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, null, tint = Color.White.copy(0.4f))
                    }
                }
            }

            when (val state = uiState) {
                is JourneyUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CyanNeon)
                    }
                }
                is JourneyUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.Red)
                    }
                }
                is JourneyUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section "Today" / Highlight
                        item {
                            if (state.currentJourney != null) {
                                TodayCard(
                                    journey = state.currentJourney.first,
                                    userState = state.currentJourney.second,
                                    onClick = { onJourneyClick(state.currentJourney.first.id) }
                                )
                            } else if (state.recommendedJourney != null) {
                                RecommendedCard(
                                    journey = state.recommendedJourney,
                                    onClick = { onJourneyClick(state.recommendedJourney.id) }
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "EXPLORAR TRILHAS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(0.4f),
                                letterSpacing = 2.sp
                            )
                        }

                        items(state.journeys) { journey ->
                            JourneyCard(journey, onClick = { onJourneyClick(journey.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodayCard(journey: Journey, userState: com.totem.ia.domain.model.UserJourneyState, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = PurpleNeon.copy(0.15f),
        border = BorderStroke(2.dp, PurpleNeon.copy(0.3f))
    ) {
        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("CONTINUAR HOJE", color = PurpleNeon, style = MaterialTheme.typography.labelSmall)
                Text(journey.title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text("Episódio ${userState.currentChapter + 1}", color = Color.White.copy(0.6f))
            }
            Icon(Icons.Default.PlayArrow, null, tint = PurpleNeon, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun RecommendedCard(journey: Journey, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = CyanNeon.copy(0.15f),
        border = BorderStroke(2.dp, CyanNeon.copy(0.3f))
    ) {
        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("COMECE POR AQUI", color = CyanNeon, style = MaterialTheme.typography.labelSmall)
                Text(journey.title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text("Sugerido para você", color = Color.White.copy(0.6f))
            }
            Icon(Icons.Default.ChevronRight, null, tint = CyanNeon, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun JourneyCard(journey: Journey, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = Color.White.copy(0.05f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    journey.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanNeon
                )
                Text(
                    journey.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    journey.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f),
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    journey.durationType,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.4f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(0.2f)
            )
        }
    }
}
