package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.requests.CreatePostRequest
import pw.coding.data.requests.DeletePostRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.LikeService
import pw.coding.service.PostService
import pw.coding.service.UserService
import pw.coding.util.ApiResponseMessages
import pw.coding.util.Constants
import pw.coding.util.QueryParams

fun Route.createPost(
    postService: PostService
) {
    authenticate {
        post("/api/post/create") {
            val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userId = call.userID
            val didUserExists = postService.createPostIfUserExists(request , userId)
            if (!didUserExists) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            } else {
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

fun Route.getPostForFollows(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        get("/api/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]
                ?.toIntOrNull() ?: Constants.PAGE_SIZE

            val posts = postService.getPostsByFollows(ownUserId = call.userID , page , pageSize)
            call.respond(
                HttpStatusCode.OK ,
                posts
            )
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    likeService: LikeService
){
    authenticate {
        delete ("/api/post/delete") {
            val request = call.receiveNullable<DeletePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val post = postService.getPost(request.postId)
            if(post==null){
                call.respond(
                    HttpStatusCode.NotFound
                )
                return@delete
            }
            if(post.userId == call.userID){
                postService.deletePost(request.postId)
                likeService.deleteLikesForParent(request.postId)
                call.respond(HttpStatusCode.OK)
            }else{
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}

