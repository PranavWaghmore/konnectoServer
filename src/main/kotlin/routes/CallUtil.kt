package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import pw.coding.plugins.email


//PipelineContext<Unit,ApplicationCall>.ifEmailBelongsToUser
suspend fun ifEmailBelongsToUSer(
    call: ApplicationCall,
    userId : String,
    validateEmail: suspend (email: String , userID: String) -> Boolean,
    onSuccess: suspend ()-> Unit
){
    val isEmailByUser = validateEmail(
        call.principal<JWTPrincipal>()?.email ?: "",
        userId
    )
     if(isEmailByUser){
         onSuccess()
     }else{
         call.respond(HttpStatusCode.Unauthorized)
     }
}