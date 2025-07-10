package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.requests.CreateCommentRequest
import pw.coding.data.requests.DeleteCommentRequest
import pw.coding.data.requests.DeletePostRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.CommentService
import pw.coding.service.LikeService
import pw.coding.service.UserService
import pw.coding.util.ApiResponseMessages
import pw.coding.util.QueryParams

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


fun Route.getCommentsForPost(
    commentService: CommentService
){
    authenticate {
        get("/api/comment/get"){
            val postId = call.pathParameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val comments = commentService.getCommentsForPost(postId)
            call.respond(HttpStatusCode.OK)
        }
    }
}


fun Route.deleteComment(
    commentService: CommentService,
    userService: UserService,
    likeService: LikeService
){
   authenticate {
       delete("/api/comment/delete"){
           val request = call.receiveNullable<DeleteCommentRequest>() ?: kotlin.run {
               call.respond(HttpStatusCode.BadRequest)
               return@delete
           }

           ifEmailBelongsToUSer(
               call=call,
               userId = request.userId,
               validateEmail = userService::doesEmailBelongsToUserId
           ){
               val deleteComment = commentService.deleteComment(request.commentId)
               if(deleteComment){
                   likeService.deleteLikesForParent(request.commentId)
                   call.respond(HttpStatusCode.OK, BasicApiResponse(successful = true))
               }else{
                   call.respond(HttpStatusCode.OK, BasicApiResponse(successful = false))
               }
           }
       }
   }
}