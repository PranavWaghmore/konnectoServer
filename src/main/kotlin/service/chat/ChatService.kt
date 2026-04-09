package pw.coding.service.chat

import pw.coding.data.models.Message
import pw.coding.data.repository.chat.ChatRepository
import pw.coding.data.responses.ChatDto

class ChatService(
    private val repository: ChatRepository
) {

    suspend fun getMessagesForChat(chatId: String, page: Int, pageSize: Int): List<Message>{
        return repository.getMessagesForChat(
            chatId,
            page,
            pageSize
        )
    }

    suspend fun getChatsForUser(ownUserId: String): List<ChatDto>{
        return repository.getChatsForUser(ownUserId)
    }

    suspend fun doesChatBelongToUser(chatId: String, userId: String): Boolean{
        return repository.doesChatBelongToUser(chatId, userId)
    }
}