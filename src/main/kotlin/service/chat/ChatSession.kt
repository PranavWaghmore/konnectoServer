package pw.coding.service.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatSession(
    val userId: String,
    val sessionId: String,
)