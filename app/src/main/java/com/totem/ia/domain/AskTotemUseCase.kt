package com.totem.ia.domain

import com.totem.ia.data.RemoteChatDataSource
import com.totem.ia.data.SettingsManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Single-responsibility use-case: asks the AI a question.
 *
 * The server URL is fixed in [com.totem.ia.di.NetworkModule].
 * Only the system prompt is read from [SettingsManager] at runtime.
 *
 * Returns a [Result] propagated from [RemoteChatDataSource].
 */
class AskTotemUseCase @Inject constructor(
    private val dataSource: RemoteChatDataSource,
    private val settings: SettingsManager
) {
    suspend operator fun invoke(sessionId: String, userText: String): Result<String> {
        val systemPrompt = settings.systemPromptFlow.first()
        return dataSource.sendMessage(sessionId, userText, systemPrompt)
    }
}
