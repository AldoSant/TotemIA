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

private val BgDeep      = Color(0xFF020205)
private val BgGlass     = Color(0x770A0A15)
private val PurpleNeon  = Color(0xFF8B5CF6)
private val CyanNeon    = Color(0xFF06B6D4)
private val RedNeon     = Color(0xFFEF4444)
private val GreenNeon   = Color(0xFF10B981)

private val TotemState.color get() = when (this) {
    TotemState.READY     -> PurpleNeon
    TotemState.LISTENING -> RedNeon
    TotemState.THINKING  -> CyanNeon
    TotemState.SPEAKING  -> GreenNeon
}

// ── World-Class Visualizer ────────────────────────────────────────────────────

@Composable
fun NeuralOrb(state: TotemState, isListening: Boolean, rms: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "neural")
    
    val baseRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing))
    )

    val waveAmplitude by animateFloatAsState(
        targetValue = if (isListening) (rms * 2f).coerceIn(10f, 100f) else 15f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(320.dp)) {
        // Global Glow
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to state.color.copy(alpha = 0.2f),
                    0.6f to state.color.copy(alpha = 0.05f),
                    1.0f to Color.Transparent
                ),
                radius = size.width / 2
            )
        }

        // Layered Wave Rings
        repeat(4) { i ->
            val phaseShift = (i * PI / 2).toFloat()
            val speed = 2000 + (i * 500)
            val time by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = (2 * PI).toFloat(),
                animationSpec = infiniteRepeatable(tween(speed, easing = LinearEasing))
            )

            Canvas(Modifier.fillMaxSize().graphicsLayer { rotationZ = baseRotation * (if(i%2==0) 1 else -1) }) {
                val center = size / 2f
                val radius = 90.dp.toPx() + (i * 12.dp.toPx())
                val path = Path()
                
                for (angle in 0..360 step 5) {
                    val rad = Math.toRadians(angle.toDouble()).toFloat()
                    val wave = sin(rad * (i + 2) + time + phaseShift) * waveAmplitude
                    val x = center.width + (radius + wave) * cos(rad)
                    val y = center.height + (radius + wave) * sin(rad)
                    
                    if (angle == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                
                drawPath(
                    path = path,
                    color = state.color.copy(alpha = 0.4f / (i + 1)),
                    style = Stroke(width = (4 - i).coerceAtLeast(1).dp.toPx(), cap = StrokeCap.Round)
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
                    TotemState.READY -> Icons.Default.Info
                    TotemState.LISTENING -> Icons.Default.Mic
                    TotemState.THINKING -> Icons.Default.Refresh
                    TotemState.SPEAKING -> Icons.Default.PlayArrow
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

// ── Screen Implementation ─────────────────────────────────────────────────────

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .drawBehind {
                drawRect(Brush.radialGradient(listOf(state.color.copy(0.08f), Color.Transparent)))
            }
    ) {
        Column(Modifier.fillMaxSize()) {
            // Header Premium
            Row(
                Modifier.fillMaxWidth().padding(24.dp).statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("QUANTUM", style = MaterialTheme.typography.labelSmall, color = state.color, letterSpacing = 4.sp)
                    Text("TOTEM AI", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Black)
                }
                Row {
                    IconButton(onClick = { viewModel.clearHistory() }) { Icon(Icons.Default.History, null, tint = Color.White.copy(0.4f)) }
                    IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Menu, null, tint = Color.White.copy(0.4f)) }
                }
            }

            // Visualizer Section
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                NeuralOrb(state, isListening, rmsLevel)
            }

            // Chat & Input Section
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1.2f),
                color = BgGlass,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                border = BorderStroke(1.dp, Color.White.copy(0.05f))
            ) {
                Column(Modifier.fillMaxSize().padding(top = 24.dp)) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(messages) { msg ->
                            WorldClassBubble(msg)
                        }
                    }

                    // Immersive Input
                    Row(
                        Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding().imePadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Advanced Mic Orb
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(24.dp, CircleShape, spotColor = state.color)
                                .clip(CircleShape)
                                .background(if (isListening) RedNeon else PurpleNeon)
                                .pointerInput(Unit) { detectTapGestures { 
                                    if (!hasMicPermission) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    else viewModel.toggleListening()
                                } },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Seamless Input
                        TextField(
                            value = if (isListening) partialText else inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(if (isListening) "Listening..." else "Message Totem...", color = Color.White.copy(0.2f)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(0.03f),
                                unfocusedContainerColor = Color.White.copy(0.03f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            enabled = !isListening,
                            trailingIcon = {
                                if (inputText.isNotBlank()) {
                                    IconButton(onClick = {
                                        viewModel.sendMessage(inputText)
                                        inputText = ""
                                    }) { Icon(Icons.Default.NorthEast, null, tint = CyanNeon) }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorldClassBubble(message: Message) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    
    Box(Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = if (message.isUser) PurpleNeon.copy(0.1f) else Color.White.copy(0.03f),
            shape = RoundedCornerShape(
                topStart = 24.dp, topEnd = 24.dp,
                bottomStart = if (message.isUser) 24.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 24.dp
            ),
            border = BorderStroke(1.dp, if (message.isUser) PurpleNeon.copy(0.3f) else Color.White.copy(0.05f)),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isError) RedNeon else Color.White,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
            )
        }
    }
}
