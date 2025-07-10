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
    postService: PostService,
    userService: UserService,
) {
    authenticate {
        post("/api/post/create") {
            val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

//            val email = call.principal<JWTPrincipal>()?.getClaim(name = "email", String::class)
//            val isEmailByUser = userService.doesEmailBelongsToUserId(
//                email = email ?: "",
//                userId = request.userId
//            )
//            if (!isEmailByUser) {
//                call.respond(HttpStatusCode.Unauthorized, "You are not who say You are.")
//                return@post
//            }
           ifEmailBelongsToUSer(
               call = call,
               userId = request.userId,
               validateEmail = userService::doesEmailBelongsToUserId
           ){
               val didUserExists = postService.createPostIfUserExists(request)
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
}

fun Route.getPostForFollows(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        get {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]
                ?.toIntOrNull() ?: Constants.PAGE_SIZE

            ifEmailBelongsToUSer(
                call = call,
                userId = userId,
                validateEmail = userService::doesEmailBelongsToUserId
            ){
                val posts = postService.getPostsByFollows(userId , page , pageSize)
                call.respond(
                    HttpStatusCode.OK ,
                    posts
                )
            }
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    userService: UserService,
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
//            val email = call.principal<JWTPrincipal>()?.getClaim(name = "email", String::class)
//            val isEmailByUser = userService.doesEmailBelongsToUserId(
//                email = email ?: "",
//                userId = post.userId
//            )
//            if (!isEmailByUser) {
//                call.respond(HttpStatusCode.Unauthorized, "You are not who say You are.")
//                return@delete
//            }
             ifEmailBelongsToUSer(
                 call = call,
                 userId = post.userId,
                 validateEmail = userService::doesEmailBelongsToUserId
             ){
                 postService.deletePost(request.postId)
                 likeService.deleteLikesForParent(request.postId)
                 call.respond(HttpStatusCode.OK)
             }
        }
    }
}

