package pw.coding.plugins


import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.generateNonce
import pw.coding.service.chat.ChatSession

fun Application.configureSessions(){
    install(Sessions){
        cookie<ChatSession>("SESSION")
    }


    //Its job is:
    //check whether the user already has a session
    //if not, create one and store it
    intercept(ApplicationCallPipeline.Plugins) {   // If session doesn't exist to chat it creates one
        val existingSession = call.sessions.get<ChatSession>()
        if (existingSession == null) {
            val userId = call.parameters["userId"] ?: return@intercept
            call.sessions.set(ChatSession(userId = userId, sessionId = generateNonce()))
        }
    }
}