package pw.coding.routes

import data.repository.follow.FollowRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.requests.FollowUpdateRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.util.ApiResponseMessages.USER_NOT_FOUND

fun Route.followUser(
    followRepository: FollowRepository
){
    post("/api/following/follow") {
        val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val didUserExists = followRepository.followUserIfExists(
            request.followingUserId,
            request.followedUserId
        )
        if(didUserExists){
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(
                    successful = true
                )
            )
        }else{
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(
                    successful = false,
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}


fun Route.unfollowUser(
    followRepository: FollowRepository
){
    delete("/api/following/unfollow"){
        val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }
        val didUserExists = followRepository.unfollowUserIfExists(
            request.followingUserId,
            request.followedUserId
        )
        if(didUserExists){
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(
                    successful = true
                )
            )
        }else{
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(
                    successful = false,
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}