package com.totem.ia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.totem.ia.data.local.ReflectionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BgDeep = Color(0xFF020205)
private val PurpleNeon = Color(0xFF8B5CF6)
private val CardBg = Color(0x1AFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    onNavigateBack: () -> Unit,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val reflections by viewModel.reflections.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diário de Bordo", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDeep)
            )
        },
        containerColor = BgDeep
    ) { padding ->
        if (reflections.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Seu diário está vazio. Complete jornadas para preenchê-lo.", color = Color.White.copy(0.6f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reflections) { reflection ->
                    ReflectionCard(reflection)
                }
            }
        }
    }
}

@Composable
fun ReflectionCard(reflection: ReflectionEntity) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(reflection.timestamp))

    Surface(
        color = CardBg,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = dateString,
                style = MaterialTheme.typography.labelSmall,
                color = PurpleNeon
            )
            Spacer(Modifier.height(8.dp))
            
            Text("Você:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Text(reflection.userText, color = Color.White.copy(0.8f), fontSize = 14.sp)
            
            Spacer(Modifier.height(12.dp))
            
            Text("Totem:", fontWeight = FontWeight.Bold, color = PurpleNeon, fontSize = 14.sp)
            Text(reflection.aiResponse, color = Color.White.copy(0.8f), fontSize = 14.sp)
        }
    }
}
