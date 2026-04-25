package pw.coding.service.chat

import com.google.gson.Gson
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import pw.coding.data.repository.chat.ChatRepository
import pw.coding.data.webSocket.WsClientMessage
import pw.coding.data.webSocket.WsServerMessage
import pw.coding.util.WebSocketObject
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val repository: ChatRepository
) {

    private val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(userId: String, socket: WebSocketSession){
        onlineUsers[userId] = socket
    }

    fun onDisconnect(userId: String){
        if(onlineUsers.contains(userId)){
            onlineUsers.remove(userId)
        }
    }

    suspend fun sendMessage(ownUserId: String, gson: Gson, message: WsClientMessage){
        val messageEntity = message.toMessage(ownUserId)
        val wsSerMessage = WsServerMessage(
            fromId = messageEntity.fromId,
            toId = messageEntity.toId,
            text = messageEntity.text,
            timestamp = messageEntity.timestamp,
            chatId = messageEntity.chatId
        )
        val frameText = gson.toJson(wsSerMessage)
        val frame = Frame.Text("${WebSocketObject.MESSAGE.ordinal}#$frameText")
        onlineUsers[ownUserId]?.send(frame)
        if (message.toId != ownUserId) {
            onlineUsers[message.toId]?.send(frame)
        }

        if(!repository.doesChatByUsersExist(ownUserId,messageEntity.toId)){
            val chatId = repository.insertChat(ownUserId, message.toId, messageEntity.id)
            repository.insertMessage(messageEntity.copy(chatId = chatId))
        }else{
            repository.insertMessage(messageEntity)
            message.chatId?.let {
                repository.updateLastMessageIdForChat(
                    chatId = message.chatId,
                    lastMessageId = messageEntity.id
                )
            }
        }
    }
}