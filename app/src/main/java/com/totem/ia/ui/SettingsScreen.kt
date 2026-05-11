package com.totem.ia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val BgDeep = Color(0xFF030308)
private val PurpleNeon = Color(0xFF7C4DFF)
private val CyanNeon = Color(0xFF00E5FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val baseUrl      by viewModel.baseUrl.collectAsState()
    val systemPrompt by viewModel.systemPrompt.collectAsState()

    Scaffold(
        containerColor = BgDeep,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text("SYSTEM CONFIG", 
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsCard(
                title = "Server Endpoint",
                icon = Icons.Default.Dns,
                color = CyanNeon
            ) {
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { viewModel.updateBaseUrl(it) },
                    placeholder = { Text("http://192.168.1.7:18790", color = Color.White.copy(0.3f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = Color.White.copy(0.1f)
                    )
                )
            }

            SettingsCard(
                title = "AI Personality",
                icon = Icons.Default.Psychology,
                color = PurpleNeon
            ) {
                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { viewModel.updateSystemPrompt(it) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PurpleNeon,
                        unfocusedBorderColor = Color.White.copy(0.1f)
                    )
                )
            }

            SettingsCard(
                title = "Daily Reminder",
                icon = androidx.compose.material.icons.filled.Notifications,
                color = Color(0xFFFFB74D)
            ) {
                OutlinedTextField(
                    value = viewModel.notificationTime.collectAsState().value,
                    onValueChange = { viewModel.updateNotificationTime(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("07:00", color = Color.White.copy(0.3f)) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFB74D),
                        unfocusedBorderColor = Color.White.copy(0.1f)
                    )
                )
            }

            Spacer(Modifier.height(40.dp))
            
            Button(
                onClick = { viewModel.testVoice() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.05f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(0.1f))
            ) {
                Icon(Icons.Default.VolumeUp, null, tint = Color.White)
                Spacer(Modifier.width(12.dp))
                Text("TESTAR VOZ DO TOTEM", color = Color.White)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "TOTEM OS v1.0.4 - SECURE BUILD",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.2f)
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(title.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 12.sp)
        }
        content()
    }
}
