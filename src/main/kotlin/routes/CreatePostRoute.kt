package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.requests.CreatePostRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.PostService
import pw.coding.service.UserService
import pw.coding.util.ApiResponseMessages

fun Route.createPostRoute(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        post("/api/post/create") {
            val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val email = call.principal<JWTPrincipal>()?.getClaim(name = "email",String::class)
            val isEmailByUser = userService.doesEmailBelongsToUserId(
                email = email ?: "",
                userId = request.userId
            )
            if(!isEmailByUser){
                call.respond(HttpStatusCode.Unauthorized , "You are not who say You are.")
                return@post
            }
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