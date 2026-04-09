package pw.coding.service.chat

import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import pw.coding.data.models.Message
import pw.coding.data.repository.chat.ChatRepository
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val repository: ChatRepository
) {

    private val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(chatSession: ChatSession, socket: WebSocketSession){
        onlineUsers[chatSession.userId] = socket
    }

    fun onDisconnect(userId: String){
        if(onlineUsers.contains(userId)){
            onlineUsers.remove(userId)
        }
    }

    suspend fun sendMessage(json: String , message: Message){
        onlineUsers[message.toId]?.send(Frame.Text(json))
        onlineUsers[message.fromId]?.send(Frame.Text(json))
        val message = message
        repository.insertMessage(message)
        if(!repository.doesChatByUsersExist(message.fromId,message.toId)){
            repository.insertChat(message.fromId,message.toId, messageId = message.id)
        }else{
            message.chatId?.let {
                repository.updateLastMessageIdForChat(
                    chatId = message.chatId,
                    lastMessageId = message.id
                )
            }
        }
    }
}