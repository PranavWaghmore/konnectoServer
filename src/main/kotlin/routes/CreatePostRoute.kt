package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.models.Post
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.requests.CreatePostRequest
import pw.coding.data.requests.LoginRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.PostService
import pw.coding.util.ApiResponseMessages

fun Route.createPostRoute(postService: PostService) {
    post("/api/post/create") {
        val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val didUserExists = postService.createPostIfUserExists(request)
        if (!didUserExists) {
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(
                    successful = false,
                    message = ApiResponseMessages.USER_NOT_FOUND
                )
            )
        } else {
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(
                    successful = true
                )
            )
        }
    }
}