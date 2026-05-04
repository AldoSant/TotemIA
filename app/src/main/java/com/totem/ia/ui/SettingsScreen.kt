package com.totem.ia.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val baseUrl by viewModel.baseUrl.collectAsState()
    val systemPrompt by viewModel.systemPrompt.collectAsState()
    val availableVoices by viewModel.availableVoices.collectAsState()
    val selectedVoiceName by viewModel.selectedVoiceName.collectAsState()
    var voiceDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações do Totem") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // URL Section
            Text("Conexão", style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = baseUrl,
                onValueChange = { viewModel.updateBaseUrl(it) },
                label = { Text("URL do servidor (OpenClaw)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("http://192.168.1.7:18790") }
            )

            HorizontalDivider()

            // Personality Section
            Text("Personalidade", style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = systemPrompt,
                onValueChange = { viewModel.updateSystemPrompt(it) },
                label = { Text("System Prompt (personalidade da IA)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )

            HorizontalDivider()

            // Voice Section
            Text("Voz", style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary)

            if (availableVoices.isEmpty()) {
                Text(
                    "Carregando vozes disponíveis...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                ExposedDropdownMenuBox(
                    expanded = voiceDropdownExpanded,
                    onExpandedChange = { voiceDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = if (selectedVoiceName.isBlank()) "Voz padrão do sistema" else selectedVoiceName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Voz selecionada") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = voiceDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = voiceDropdownExpanded,
                        onDismissRequest = { voiceDropdownExpanded = false }
                    ) {
                        availableVoices.forEach { voice ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(voice.name, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "Qualidade: ${voice.quality}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.selectVoice(voice)
                                    voiceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.previewVoice() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null,
                            modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ouvir prévia da voz")
                    }
                }
            }

            HorizontalDivider()

            Text(
                "As configurações são salvas automaticamente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
