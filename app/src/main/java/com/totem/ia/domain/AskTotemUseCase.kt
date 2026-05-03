package com.totem.ia.domain

import com.totem.ia.data.RemoteChatDataSource
import javax.inject.Inject

class AskTotemUseCase @Inject constructor(
    private val remoteChatDataSource: RemoteChatDataSource
) {
    suspend operator fun invoke(sessionId: String, text: String): String {
        return remoteChatDataSource.sendMessage(sessionId, text)
    }
}
