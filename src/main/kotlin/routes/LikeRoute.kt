package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.requests.LikeUpdateRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.LikeService
import pw.coding.service.UserService
import pw.coding.util.ApiResponseMessages

fun Route.likeParent(
    likeService: LikeService
) {
    authenticate {
        post("/api/like") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val likeSuccessful = likeService.likeParent(call.userID, request.parentId)
            if (likeSuccessful) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = true
                    )
                )
            }else{
                call.respond(HttpStatusCode.OK ,
                    BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}

fun Route.unlikeParent(
    likeService: LikeService
) {
    authenticate {
        delete("/api/unlike") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val unlikeSuccessful = likeService.unlinkParent(call.userID, request.parentId)
            if (unlikeSuccessful) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = true
                    )
                )
            }else{
                call.respond(HttpStatusCode.OK ,
                    BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}