package pw.coding.routes

import io.ktor.client.request.request
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.requests.CreateCommentRequest
import pw.coding.data.requests.DeleteCommentRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.ActivityService
import pw.coding.service.CommentService
import pw.coding.service.LikeService
import pw.coding.util.ApiResponseMessages
import pw.coding.util.QueryParams

fun Route.createComment(
    commentService: CommentService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/comment/create") {
            val request = call.receiveNullable<CreateCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.userId
            when ( commentService.createComment(request, userId)) {

                is CommentService.ValidationEvent.ErrorFieldEmpty ->
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.FIELDS_BLANK
                        )
                    )

                is CommentService.ValidationEvent.ErrorCommentTooLong ->
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.COMMENT_TOO_LONG
                        )
                    )

                is CommentService.ValidationEvent.Success ->{
                    activityService.addCommentActivity(
                        byUserId =userId,
                        postId = request.postId,
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
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
) {
    authenticate {
        get("/api/comment/get") {
            val postId = call.queryParameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest,"Null postId")
                return@get
            }
            val comments = commentService.getCommentsForPost(postId, call.userId)
            call.respond(HttpStatusCode.OK , comments)
        }
    }
}


fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService
) {
    authenticate {
        delete("/api/comment/delete") {
            val request = call.receiveNullable<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val comment = commentService.getCommentById(request.commentId)
            if(comment?.userId != call.userId){
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val commentDeleted = commentService.deleteComment(request.commentId)
            if (commentDeleted) {
                likeService.deleteLikesForParent(request.commentId)
                call.respond(HttpStatusCode.OK, BasicApiResponse<Unit>(successful = true))
            } else {
                call.respond(HttpStatusCode.OK, BasicApiResponse<Unit>(successful = false))
            }
        }
    }
}