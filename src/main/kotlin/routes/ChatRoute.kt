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
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import pw.coding.data.models.Message
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


fun Route.chatWebSocket(chatController: ChatController){
    webSocket("/api/chat/websocket") {
        val session = call.sessions.get<ChatSession>()
        if(session == null){
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY,"No Session"))
            return@webSocket
        }
        chatController.onJoin(session,this)
        try {
            incoming.consumeEach { frame ->
                when(frame){
                    is Frame.Text ->{
                        val frameText = frame.readText()
                        val delimiterIndex = frameText.indexOf('#')
                        if(delimiterIndex == -1){
                            return@consumeEach
                        }
                        val type = frameText.substring(0,delimiterIndex + 1).toIntOrNull() ?: return@consumeEach
                        val json = frameText.substring(delimiterIndex,frameText.length - 1)
                        handleWebSocket(this,session,chatController,type,json)
                    }
                    else -> Unit
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Error"))
        }finally {
            chatController.onDisconnect(session.userId)
        }
    }
}


suspend fun handleWebSocket(
    webSocketSession: WebSocketSession,
    session: ChatSession,
    chatController: ChatController,
    type: Int,
    json: String
){
    val gson: Gson by inject(Gson::class.java)
    when(type){

        WebSocketObject.MESSAGE.ordinal -> {
            val message = gson.fromJsonOrNull(json, Message::class.java) ?: return
           return  chatController.sendMessage(message = message , json = json)
        }
    }
}