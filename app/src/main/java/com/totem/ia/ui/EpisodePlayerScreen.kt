package com.totem.ia.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val BgDeep = Color(0xFF020205)
private val BgGlass = Color(0x770A0A15)
private val PurpleNeon = Color(0xFF8B5CF6)
private val RedNeon = Color(0xFFEF4444)

@Composable
fun EpisodePlayerScreen(
    onClose: () -> Unit,
    viewModel: EpisodeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stage = uiState.stage
    val journey = uiState.journey
    val chapter = uiState.chapter
    val totemState = uiState.totemState
    val isListening = uiState.isListening
    val rmsLevel = uiState.rmsLevel
    val messages = uiState.messages
    val connectedDeviceName = uiState.connectedDeviceName

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
                    Text(
                        journey?.title?.uppercase() ?: "JORNADA",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleNeon,
                        letterSpacing = 2.sp
                    )
                    Text(
                        chapter?.title ?: "Carregando...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Bluetooth Status
                    Text(
                        text = connectedDeviceName?.let { "Tocando em: $it" } ?: "Alto-falante Bluetooth não conectado",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (connectedDeviceName != null) Color(0xFF10B981) else Color.White.copy(0.4f)
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, null, tint = Color.White.copy(0.4f))
                }
            }

            // Visualizer
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                NeuralOrb(totemState, isListening, rmsLevel)
                
                if (stage == EpisodeStage.PLAYING_INTRO) {
                    Text(
                        "Ouvindo o Totem...",
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
                        color = Color.White.copy(0.4f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Interaction Area
            AnimatedVisibility(
                visible = stage == EpisodeStage.REFLECTING,
                enter = slideInVertically { it } + fadeIn(),
                modifier = Modifier.weight(1.2f)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgGlass,
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "REFLEXÃO GUIADA",
                            style = MaterialTheme.typography.labelSmall,
                            color = PurpleNeon,
                            letterSpacing = 2.sp
                        )
                        
                        LazyColumn(
                            modifier = Modifier.weight(1f).padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(messages) { msg ->
                                ReflectionBubble(msg)
                            }
                        }

                        // Actions Area
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mic Button
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .shadow(24.dp, CircleShape, spotColor = totemState.color)
                                    .clip(CircleShape)
                                    .background(if (isListening) RedNeon else PurpleNeon)
                                    .clickable { viewModel.toggleListening() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            
                            Spacer(Modifier.width(24.dp))
                            
                            Button(
                                onClick = { viewModel.finishEpisode() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Concluir", color = Color.White)
                            }
                        }
                        
                        Spacer(Modifier.navigationBarsPadding())
                    }
                }
            }

            // Completion Overlay
            if (stage == EpisodeStage.COMPLETED) {
                Box(
                    Modifier.fillMaxSize().background(BgDeep.copy(0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("JORNADA CONCLUÍDA", color = PurpleNeon, style = MaterialTheme.typography.labelSmall)
                        Text("Ótimo trabalho!", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)) {
                            Text("Voltar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReflectionBubble(message: Message) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    Box(Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = if (message.isUser) PurpleNeon.copy(0.1f) else Color.White.copy(0.05f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
