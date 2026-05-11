package com.totem.ia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ProgressScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "SEU PROGRESSO",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleNeon,
                        letterSpacing = 4.sp
                    )
                    Text(
                        "Evolução",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
                
                // Streak Badge
                Spacer(Modifier.weight(1f))
                Surface(
                    color = Color(0xFFFFB74D).copy(0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(0.3f))
                ) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        val streak = (uiState as? ProgressUiState.Success)?.totalStreak ?: 0
                        Text(
                            "$streak DIAS",
                            color = Color(0xFFFFB74D),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(24.dp))
            }

            when (val state = uiState) {
                is ProgressUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CyanNeon)
                    }
                }
                is ProgressUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.Red)
                    }
                }
                is ProgressUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.journeys) { journey ->
                            ProgressCard(journey, state.userStates[journey.id])
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(journey: Journey, userState: com.totem.ia.domain.model.UserJourneyState?) {
    val progress = userState?.progressPercent?.toFloat()?.div(100f) ?: 0f

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(0.03f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(journey.title, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = CyanNeon,
                trackColor = Color.White.copy(0.1f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "${(progress * 100).toInt()}% concluído",
                color = Color.White.copy(0.4f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
