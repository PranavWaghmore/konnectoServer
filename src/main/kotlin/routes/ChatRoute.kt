package pw.coding.routes

import com.google.gson.Gson
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import org.koin.java.KoinJavaComponent.inject
import pw.coding.data.models.Message
import pw.coding.data.webSocket.WsClientMessage
import pw.coding.service.chat.ChatController
import pw.coding.service.chat.ChatService
import pw.coding.service.chat.ChatSession
import pw.coding.util.QueryParams
import pw.coding.util.WebSocketObject
import pw.coding.util.fromJsonOrNull
import kotlin.getValue

fun Route.getMessagesForChat(
    chatService: ChatService
){
    authenticate{
        get("/api/chat/messages"){
            val chatId = call.parameters[QueryParams.PARAM_CHAT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: 0


            if(!chatService.doesChatBelongToUser(chatId,call.userId)){
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }

            val messages = chatService.getMessagesForChat(chatId,page,pageSize)
            call.respond(HttpStatusCode.OK, message = messages)
        }
    }
}

fun Route.getChatsForUser(chatService: ChatService){
    authenticate {
        get("/api/chats"){
            val chats = chatService.getChatsForUser(call.userId)
            call.respond(
                HttpStatusCode.OK,
                chats
            )
        }
    }
}


fun Route.chatWebSocket(chatController: ChatController, gson: Gson){
    authenticate {
        webSocket("/api/chat/websocket") {
            println("Connecting via web socket")
            chatController.onJoin(call.userId,this)
            try {
                incoming.consumeEach { frame ->
                    when(frame){
                        is Frame.Text ->{
                            val frameText = frame.readText()
                            val delimiterIndex = frameText.indexOf('#')
                            println("frameText is $frameText")
                            if(delimiterIndex == -1){
                                println("In Delimeter $delimiterIndex")
                                return@consumeEach
                            }
                            println("")
                            val type = frameText.substring(0,delimiterIndex).toIntOrNull()
                            println("In type $type")
                            if(type == null) return@consumeEach
                            val json = frameText.substring(delimiterIndex+1)
                            handleWebSocket(
                                call.userId,chatController, gson = gson,type,frameText,json
                            )
                        }
                        else -> Unit
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
                close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Error"))
            }finally {
                chatController.onDisconnect(call.userId)
            }
        }
    }
}


suspend fun handleWebSocket(
   ownUserId: String,
    chatController: ChatController,
   gson: Gson,
    type: Int,
    frameText: String,
    json: String
){
    when(type){
        WebSocketObject.MESSAGE.ordinal -> {
            val message = gson.fromJsonOrNull(json, WsClientMessage::class.java) ?: return
            println("Message is $message")
           return  chatController.sendMessage(ownUserId,gson, message = message)
        }
    }
}