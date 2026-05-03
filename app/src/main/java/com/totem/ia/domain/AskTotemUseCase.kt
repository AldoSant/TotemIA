package com.totem.ia.domain

import com.totem.ia.data.RemoteChatDataSource
import com.totem.ia.data.SettingsManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AskTotemUseCase @Inject constructor(
    private val remoteChatDataSource: RemoteChatDataSource,
    private val settingsManager: SettingsManager
) {
    suspend operator fun invoke(sessionId: String, text: String): String {
        val baseUrl = settingsManager.baseUrlFlow.first()
        val systemPrompt = settingsManager.systemPromptFlow.first()
        return remoteChatDataSource.sendMessage(baseUrl, sessionId, text, systemPrompt)
    }
}
