package com.totem.ia.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

// Dark theme colors
private val BgDark = Color(0xFF0D0D1A)
private val BgCard = Color(0xFF1A1A2E)
private val AccentPurple = Color(0xFF7C4DFF)
private val AccentCyan = Color(0xFF00E5FF)
private val ReadyColor = Color(0xFF7C4DFF)
private val ListeningColor = Color(0xFFFF1744)
private val ThinkingColor = Color(0xFF00BCD4)
private val SpeakingColor = Color(0xFF00E676)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotemScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: TotemViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val totemState by viewModel.totemState.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasMicPermission = it }

    // Auto-scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Orb pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (totemState == TotemState.READY) 2000 else 600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbScale"
    )
    val orbAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (totemState == TotemState.READY) 2000 else 600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbAlpha"
    )

    val stateColor = when (totemState) {
        TotemState.READY -> ReadyColor
        TotemState.LISTENING -> ListeningColor
        TotemState.THINKING -> ThinkingColor
        TotemState.SPEAKING -> SpeakingColor
    }

    val stateLabel = when (totemState) {
        TotemState.READY -> "Aguardando..."
        TotemState.LISTENING -> "Ouvindo..."
        TotemState.THINKING -> "Pensando..."
        TotemState.SPEAKING -> "Falando..."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BgDark, Color(0xFF0A0A1F), BgDark)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTEM IA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 3.sp
                    )
                    Text(
                        text = stateLabel,
                        fontSize = 12.sp,
                        color = stateColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row {
                    IconButton(onClick = { viewModel.replayLastResponse() }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Repetir",
                            tint = Color.White.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações",
                            tint = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            // Orb + Status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer {
                            scaleX = orbScale
                            scaleY = orbScale
                            alpha = orbAlpha * 0.3f
                        }
                        .clip(CircleShape)
                        .background(stateColor)
                )
                // Middle ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer {
                            scaleX = orbScale * 0.95f
                            scaleY = orbScale * 0.95f
                            alpha = orbAlpha * 0.5f
                        }
                        .clip(CircleShape)
                        .background(stateColor)
                )
                // Inner core
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.9f), stateColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (totemState) {
                            TotemState.READY -> "●"
                            TotemState.LISTENING -> "◉"
                            TotemState.THINKING -> "◌"
                            TotemState.SPEAKING -> "◎"
                        },
                        fontSize = 28.sp,
                        color = Color.White
                    )
                }
            }

            // Messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
            }

            // Input bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgCard)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mic button — push-to-talk
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(if (isListening) 12.dp else 4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isListening) ListeningColor else AccentPurple)
                            .pointerInput(hasMicPermission) {
                                detectTapGestures(
                                    onPress = {
                                        if (!hasMicPermission) {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        } else {
                                            viewModel.startListening()
                                            tryAwaitRelease()
                                            viewModel.stopListening()
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Segurar para falar",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Text field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Ou digite aqui...",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentPurple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            cursorColor = AccentPurple
                        ),
                        shape = RoundedCornerShape(28.dp),
                        singleLine = true
                    )

                    // Send button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() && totemState != TotemState.THINKING)
                                    AccentCyan else Color.White.copy(alpha = 0.1f)
                            )
                            .then(
                                if (inputText.isNotBlank() && totemState != TotemState.THINKING)
                                    Modifier.pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            viewModel.sendMessage(inputText)
                                            inputText = ""
                                            coroutineScope.launch {
                                                if (messages.isNotEmpty())
                                                    listState.animateScrollToItem(messages.size - 1)
                                            }
                                        })
                                    }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = if (inputText.isNotBlank() && totemState != TotemState.THINKING)
                                Color.Black else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.isUser
    val bubbleColor = if (isUser)
        Brush.horizontalGradient(listOf(AccentPurple, Color(0xFF9C27B0)))
    else
        Brush.horizontalGradient(listOf(BgCard, Color(0xFF1E1E35)))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AccentCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("T", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = if (isUser) 20.dp else 4.dp,
                        topEnd = if (isUser) 4.dp else 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }

        if (isUser) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text("U", color = AccentPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
