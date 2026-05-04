package com.totem.ia.domain

import com.totem.ia.data.RemoteChatDataSource
import com.totem.ia.data.SettingsManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Single-responsibility use-case: asks the AI a question.
 *
 * Reads the server URL and system prompt from [SettingsManager] so the user
 * can change them at runtime without restarting the app.
 *
 * Returns a [Result] propagated from [RemoteChatDataSource].
 */
class AskTotemUseCase @Inject constructor(
    private val dataSource: RemoteChatDataSource,
    private val settings: SettingsManager
) {
    suspend operator fun invoke(sessionId: String, userText: String): Result<String> {
        val baseUrl      = settings.baseUrlFlow.first()
        val systemPrompt = settings.systemPromptFlow.first()
        return dataSource.sendMessage(baseUrl, sessionId, userText, systemPrompt)
    }
}
