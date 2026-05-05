package com.totem.ia.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ── Design Tokens ─────────────────────────────────────────────────────────────

private val BgDeep      = Color(0xFF030308)
private val BgGlass     = Color(0xAA101025)
private val PurpleNeon  = Color(0xFF7C4DFF)
private val CyanNeon    = Color(0xFF00E5FF)
private val RedNeon     = Color(0xFFFF1744)
private val GreenNeon   = Color(0xFF00E676)
private val Gold        = Color(0xFFFFD700)

private val TotemState.color get() = when (this) {
    TotemState.READY     -> PurpleNeon
    TotemState.LISTENING -> RedNeon
    TotemState.THINKING  -> CyanNeon
    TotemState.SPEAKING  -> GreenNeon
}

private val TotemState.glow get() = color.copy(alpha = 0.4f)

// ── Components ────────────────────────────────────────────────────────────────

@Composable
fun TotemScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: TotemViewModel = hiltViewModel()
) {
    val messages    by viewModel.messages.collectAsState()
    val state       by viewModel.totemState.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val partialText by viewModel.partialText.collectAsState()
    val rmsLevel    by viewModel.rmsLevel.collectAsState()
    var inputText   by remember { mutableStateOf("") }

    val listState      = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context        = LocalContext.current

    var hasMicPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasMicPermission = it }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        containerColor = BgDeep,
        bottomBar = {
            InputSection(
                inputText = inputText,
                isListening = isListening,
                partialText = partialText,
                onInputChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                onToggleMic = {
                    if (!hasMicPermission) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    else viewModel.toggleListening()
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .drawBehind {
                    drawRect(Brush.verticalGradient(listOf(BgDeep, Color(0xFF0A0A1F), BgDeep)))
                }
        ) {
            Column(Modifier.fillMaxSize()) {
                // Header
                Header(state, onNavigateToSettings, onClear = { viewModel.clearHistory() })

                // Neural Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f),
                    contentAlignment = Alignment.Center
                ) {
                    NeuralOrb(state, isListening, rmsLevel)
                }

                // Chat History
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { msg ->
                        ChatBubblePremium(msg)
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(state: TotemState, onSettings: () -> Unit, onClear: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "TOTEM CORE",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = Color.White
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(state.color))
                Spacer(Modifier.width(8.dp))
                Text(
                    state.name.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = state.color,
                        letterSpacing = 2.sp
                    )
                )
            }
        }
        Row {
            IconButton(onClick = onClear) { Icon(Icons.Default.DeleteSweep, "Clear", tint = Color.White.copy(0.6f)) }
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Settings", tint = Color.White.copy(0.6f)) }
        }
    }
}

@Composable
private fun NeuralOrb(state: TotemState, isListening: Boolean, rms: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing))
    )

    val scale by animateFloatAsState(
        targetValue = if (isListening) 1f + (rms / 30f).coerceIn(0f, 0.5f) else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer Glow
        Canvas(modifier = Modifier.size(280.dp)) {
            drawCircle(
                Brush.radialGradient(listOf(state.color.copy(0.15f), Color.Transparent)),
                radius = size.width / 2 * scale
            )
        }

        // Rotating Rings
        repeat(3) { i ->
            val angleOffset = i * 40f
            Canvas(modifier = Modifier.size(180.dp + (i * 20).dp)) {
                drawArc(
                    color = state.color.copy(0.3f),
                    startAngle = rotation + angleOffset,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 2.dp, cap = StrokeCap.Round)
                )
            }
        }

        // Central Core
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(20.dp, CircleShape, ambientColor = state.color, spotColor = state.color)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(state.color, state.color.copy(0.4f))))
                .border(2.dp, Color.White.copy(0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when(state) {
                    TotemState.READY -> Icons.Default.Adb
                    TotemState.LISTENING -> Icons.Default.GraphicEq
                    TotemState.THINKING -> Icons.Default.AutoAwesome
                    TotemState.SPEAKING -> Icons.Default.VolumeUp
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun ChatBubblePremium(message: Message) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = RoundedCornerShape(
        topStart = 20.dp, topEnd = 20.dp,
        bottomStart = if (message.isUser) 20.dp else 4.dp,
        bottomEnd = if (message.isUser) 4.dp else 20.dp
    )

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
            Surface(
                color = if (message.isUser) PurpleNeon.copy(0.2f) else BgGlass,
                shape = shape,
                border = BorderStroke(1.dp, if (message.isUser) PurpleNeon.copy(0.5f) else Color.White.copy(0.1f)),
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (message.isError) RedNeon else Color.White,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                if (message.isUser) "VOCÊ" else "TOTEM",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(0.4f),
                modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}

@Composable
private fun InputSection(
    inputText: String,
    isListening: Boolean,
    partialText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onToggleMic: () -> Unit
) {
    Surface(
        color = BgGlass,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Glass Mic Button
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (isListening) RedNeon else PurpleNeon)
                    .pointerInput(Unit) { detectTapGestures { onToggleMic() } },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Glass Text Field
            TextField(
                value = if (isListening) partialText else inputText,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (isListening) "Ouvindo..." else "Diga algo...", color = Color.White.copy(0.3f)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(0.05f),
                    unfocusedContainerColor = Color.White.copy(0.05f),
                    disabledContainerColor = Color.White.copy(0.05f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                enabled = !isListening,
                trailingIcon = {
                    if (inputText.isNotBlank()) {
                        IconButton(onClick = onSend) {
                            Icon(Icons.Default.Send, null, tint = CyanNeon)
                        }
                    }
                }
            )
        }
    }
}
