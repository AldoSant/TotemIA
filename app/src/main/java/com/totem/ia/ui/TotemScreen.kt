package com.totem.ia.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

// ── Design tokens ─────────────────────────────────────────────────────────────

private val BgDeep    = Color(0xFF0D0D1A)
private val BgCard    = Color(0xFF1A1A2E)
private val Purple    = Color(0xFF7C4DFF)
private val Cyan      = Color(0xFF00E5FF)
private val Red       = Color(0xFFFF1744)
private val Blue      = Color(0xFF00BCD4)
private val Green     = Color(0xFF00E676)
private val ErrorRed  = Color(0xFFCF6679)

private val TotemState.color get() = when (this) {
    TotemState.READY     -> Purple
    TotemState.LISTENING -> Red
    TotemState.THINKING  -> Blue
    TotemState.SPEAKING  -> Green
}

private val TotemState.label get() = when (this) {
    TotemState.READY     -> "Aguardando..."
    TotemState.LISTENING -> "Ouvindo..."
    TotemState.THINKING  -> "Pensando..."
    TotemState.SPEAKING  -> "Falando..."
}

private val TotemState.symbol get() = when (this) {
    TotemState.READY     -> "●"
    TotemState.LISTENING -> "◉"
    TotemState.THINKING  -> "◌"
    TotemState.SPEAKING  -> "◎"
}

// ── Screens ───────────────────────────────────────────────────────────────────

@Composable
fun TotemScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: TotemViewModel = hiltViewModel()
) {
    val messages    by viewModel.messages.collectAsState()
    val state       by viewModel.totemState.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val partialText by viewModel.partialText.collectAsState()
    var inputText   by remember { mutableStateOf("") }

    val listState      = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Microphone permission
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasMicPermission = granted }

    // Auto-scroll on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    // Orb animation — faster when active
    val pulseDuration = if (state == TotemState.READY) 2000 else 600
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.90f, targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            tween(pulseDuration, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "scale"
    )
    val orbAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            tween(pulseDuration, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgDeep, Color(0xFF0A0A1F), BgDeep)))
    ) {
        Column(Modifier.fillMaxSize()) {

            // ── Top bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TOTEM IA", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, letterSpacing = 3.sp)
                    Text(state.label, fontSize = 12.sp, color = state.color,
                        fontWeight = FontWeight.Medium)
                }
                Row {
                    IconButton(onClick = { viewModel.replayLastResponse() }) {
                        Icon(Icons.Default.PlayArrow, "Repetir",
                            tint = Color.White.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = { viewModel.clearHistory() }) {
                        Icon(Icons.Default.Delete, "Limpar conversa",
                            tint = Color.White.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Configurações",
                            tint = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            // ── Animated orb ─────────────────────────────────────────────────
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    Modifier
                        .size(155.dp)
                        .graphicsLayer { scaleX = orbScale; scaleY = orbScale; alpha = orbAlpha * 0.22f }
                        .clip(CircleShape).background(state.color)
                )
                // Mid ring
                Box(
                    Modifier
                        .size(108.dp)
                        .graphicsLayer { scaleX = orbScale; scaleY = orbScale; alpha = orbAlpha * 0.45f }
                        .clip(CircleShape).background(state.color)
                )
                // Core
                Box(
                    Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color.White.copy(0.88f), state.color))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.symbol, fontSize = 26.sp, color = Color.White)
                }
            }

            // ── Messages ─────────────────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages, key = { it.hashCode() }) { msg ->
                    ChatBubble(msg)
                }
            }

            // ── Input bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgCard)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mic — push to talk
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .shadow(if (isListening) 12.dp else 4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(if (isListening) Red else Purple)
                        .pointerInput(hasMicPermission) {
                            detectTapGestures(
                                onTap = {
                                    if (!hasMicPermission) {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    } else {
                                        viewModel.toggleListening()
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Delete else Icons.Default.Mic,
                        contentDescription = if (isListening) "Parar" else "Ouvir",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Text input
                OutlinedTextField(
                    value = if (isListening) partialText else inputText,
                    onValueChange = { if (!isListening) inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { 
                        Text(
                            if (isListening) "Ouvindo..." else "Ou digite aqui...", 
                            color = Color.White.copy(0.35f), 
                            fontSize = 14.sp
                        ) 
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor        = Color.White,
                        unfocusedTextColor      = Color.White,
                        focusedBorderColor      = Purple,
                        unfocusedBorderColor    = Color.White.copy(0.2f),
                        cursorColor             = Purple
                    ),
                    shape = RoundedCornerShape(28.dp),
                    singleLine = true
                )

                // Send button
                val canSend = inputText.isNotBlank() && state != TotemState.THINKING
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (canSend) Cyan else Color.White.copy(0.1f))
                        .then(
                            if (canSend) Modifier.pointerInput(inputText) {
                                detectTapGestures(onTap = {
                                    val msg = inputText.trim()
                                    inputText = ""
                                    viewModel.sendMessage(msg)
                                    coroutineScope.launch {
                                        if (messages.isNotEmpty())
                                            listState.animateScrollToItem(messages.size - 1)
                                    }
                                })
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, "Enviar",
                        tint = if (canSend) Color.Black else Color.White.copy(0.3f),
                        modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

// ── Chat Bubble ───────────────────────────────────────────────────────────────

@Composable
fun ChatBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Avatar(label = "T", color = Cyan, modifier = Modifier.padding(end = 8.dp))
        }

        val bubbleBg = when {
            message.isError -> Brush.horizontalGradient(listOf(ErrorRed.copy(0.6f), ErrorRed))
            message.isUser  -> Brush.horizontalGradient(listOf(Purple, Color(0xFF9C27B0)))
            else            -> Brush.horizontalGradient(listOf(BgCard, Color(0xFF1E1E35)))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 285.dp)
                .clip(
                    RoundedCornerShape(
                        topStart    = if (message.isUser) 20.dp else 4.dp,
                        topEnd      = if (message.isUser) 4.dp else 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd   = 20.dp
                    )
                )
                .background(bubbleBg)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(message.text, color = Color.White, fontSize = 15.sp, lineHeight = 22.sp)
        }

        if (message.isUser) {
            Avatar(label = "U", color = Purple, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun Avatar(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
