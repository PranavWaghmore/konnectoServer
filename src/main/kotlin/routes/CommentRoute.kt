package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.requests.CreateCommentRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.CommentService
import pw.coding.service.UserService
import pw.coding.util.ApiResponseMessages

fun Route.createComment(
    commentService: CommentService,
    userService: UserService
){
    authenticate {
        post("/api/comment/create") {
            val request = call.receiveNullable<CreateCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ifEmailBelongsToUSer(
                call= call,
                userId = request.userId,
                validateEmail = userService::doesEmailBelongsToUserId
            ){
                when(commentService.createComment(request)){
                    CommentService.ValidationEvent.ErrorFieldEmpty ->
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = ApiResponseMessages.FIELDS_BLANK
                            )
                        )
                    CommentService.ValidationEvent.ErrorCommentTooLong ->
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = ApiResponseMessages.COMMENT_TOO_LONG
                            )
                        )
                    CommentService.ValidationEvent.Success ->
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = true
                            )
                        )
                }
            }
        }
    }
}