package com.totem.ia.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

private val BgDeep = Color(0xFF020205)
private val BgPanel = Color(0xFF101018)
private val PurpleNeon = Color(0xFF8B5CF6)
private val CyanNeon = Color(0xFF06B6D4)
private val RedNeon = Color(0xFFEF4444)
private val GreenNeon = Color(0xFF10B981)

@Composable
fun EpisodePlayerScreen(
    onClose: () -> Unit,
    viewModel: EpisodeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var draftMessage by remember { mutableStateOf("") }
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) viewModel.toggleListening()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            ChapterHeader(uiState, onClose)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { SessionHero(uiState) }
                item { NarrationControlCard(uiState, onToggle = viewModel::toggleNarrationPlayback) }
                item { ChapterContextCard(uiState) }
                item {
                    Text(
                        "Conversa do capítulo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Use este espaço para discordar, contar uma situação real ou pedir exemplos práticos.",
                        color = Color.White.copy(0.68f),
                        lineHeight = 21.sp,
                        fontSize = 15.sp
                    )
                }
                if (uiState.messages.isEmpty()) {
                    item {
                        ReflectionBubble(
                            Message(
                                "Quando estiver pronto, escreva ou fale o que esse capítulo mexeu em você. Eu vou te ajudar a transformar isso em clareza e próximo passo.",
                                isUser = false
                            )
                        )
                    }
                }
                items(uiState.messages) { msg -> ReflectionBubble(msg) }
                if (uiState.totemState == TotemState.THINKING) {
                    item { ReflectionBubble(Message("Estou organizando uma resposta útil para você...", isUser = false)) }
                }
            }

            InputDock(
                draftMessage = draftMessage,
                onDraftChange = { draftMessage = it },
                onSend = {
                    val textToSend = draftMessage
                    draftMessage = ""
                    viewModel.sendTextMessage(textToSend)
                },
                isListening = uiState.isListening,
                voiceStatus = uiState.voiceStatus,
                onMicClick = {
                    if (!hasMicPermission) {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        viewModel.toggleListening()
                    }
                },
                onFinish = viewModel::finishEpisode
            )
        }

        AnimatedVisibility(
            visible = uiState.stage == EpisodeStage.COMPLETED,
            enter = slideInVertically { it } + fadeIn()
        ) {
            CompletionOverlay(onClose)
        }
    }
}

@Composable
private fun ChapterHeader(uiState: EpisodeUiState, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                uiState.journey?.title?.uppercase() ?: "JORNADA",
                color = PurpleNeon,
                letterSpacing = 1.4.sp,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                uiState.chapter?.title ?: "Carregando capítulo...",
                color = Color.White,
                fontSize = 21.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Black
            )
            val deviceText = uiState.connectedDeviceName?.let { "Áudio em $it" } ?: "Áudio do dispositivo"
            Text(
                deviceText,
                color = if (uiState.connectedDeviceName != null) GreenNeon else Color.White.copy(0.48f),
                fontSize = 12.sp
            )
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White.copy(0.62f))
        }
    }
}

@Composable
private fun SessionHero(uiState: EpisodeUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(0.045f),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.08f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(82.dp),
                contentAlignment = Alignment.Center
            ) {
                NeuralOrb(
                    state = uiState.totemState,
                    isListening = uiState.isListening,
                    rms = uiState.rmsLevel
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    uiState.sessionStatus,
                    color = Color.White,
                    fontSize = 19.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    when {
                        uiState.isListening -> "Fale com naturalidade. O Totem vai usar sua fala para continuar a reflexão."
                        uiState.stage == EpisodeStage.PLAYING_INTRO -> "Você pode pausar a narração, conversar ou concluir quando quiser."
                        else -> "A conversa continua em texto ou voz, com foco em clareza e ação."
                    },
                    color = Color.White.copy(0.68f),
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
            }
        }
    }
}

@Composable
private fun NarrationControlCard(uiState: EpisodeUiState, onToggle: () -> Unit) {
    if (uiState.stage != EpisodeStage.PLAYING_INTRO) return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PurpleNeon.copy(0.10f),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, PurpleNeon.copy(0.25f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Narração da jornada",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    uiState.narrationProgressLabel.ifBlank { "Introdução do capítulo" },
                    color = Color.White.copy(0.66f),
                    fontSize = 14.sp
                )
            }
            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = if (uiState.isNarrationPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (uiState.isNarrationPlaying) "Pausar" else "Retomar")
            }
        }
    }
}

@Composable
private fun ChapterContextCard(uiState: EpisodeUiState) {
    val chapter = uiState.chapter ?: return
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BgPanel,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.06f))
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                "Objetivo do capítulo",
                color = CyanNeon,
                fontSize = 12.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                chapter.objective.replaceFirstChar { it.uppercase() },
                color = Color.White.copy(0.86f),
                fontSize = 17.sp,
                lineHeight = 25.sp
            )
        }
    }
}

@Composable
private fun InputDock(
    draftMessage: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    isListening: Boolean,
    voiceStatus: String,
    onMicClick: () -> Unit,
    onFinish: () -> Unit
) {
    Surface(
        color = BgPanel,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        tonalElevation = 8.dp
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            if (voiceStatus.isNotBlank()) {
                Text(
                    voiceStatus,
                    color = if (isListening) GreenNeon else Color.White.copy(0.62f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = draftMessage,
                onValueChange = onDraftChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Escreva sua reflexão...", color = Color.White.copy(0.36f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = PurpleNeon,
                    focusedBorderColor = PurpleNeon,
                    unfocusedBorderColor = Color.White.copy(0.14f)
                ),
                trailingIcon = {
                    IconButton(onClick = onSend, enabled = draftMessage.isNotBlank()) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = PurpleNeon)
                    }
                },
                singleLine = false,
                minLines = 1,
                maxLines = 3
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onFinish,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.10f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Concluir capítulo", color = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(12.dp, CircleShape, spotColor = if (isListening) RedNeon else PurpleNeon)
                        .clip(CircleShape)
                        .background(if (isListening) RedNeon else PurpleNeon)
                        .clickable(onClick = onMicClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "Parar microfone" else "Falar",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
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
            color = if (message.isUser) PurpleNeon.copy(0.16f) else Color.White.copy(0.07f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White.copy(0.92f),
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                lineHeight = 23.sp
            )
        }
    }
}

@Composable
private fun CompletionOverlay(onClose: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(BgDeep.copy(0.94f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("CAPÍTULO CONCLUÍDO", color = PurpleNeon, fontSize = 12.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text("Boa. Agora transforme isso em ação.", color = Color.White, fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = PurpleNeon)) {
                Text("Voltar para a jornada")
            }
        }
    }
}
