package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.models.Activity
import pw.coding.data.requests.FollowUpdateRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.data.util.ActivityType
import pw.coding.service.ActivityService
import pw.coding.service.FollowService
import pw.coding.util.ApiResponseMessages.USER_NOT_FOUND

fun Route.followUser(
    followService: FollowService,
    activityService: ActivityService
){
    authenticate {
        post("/api/following/follow") {
            val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val didUserExists = followService.followUserIfExists(request , call.userId)
            if(didUserExists){
                activityService.createActivity(
                    Activity(
                        timestamp = System.currentTimeMillis(),
                        byUserId = call.userId,
                        toUserId = request.followedUserId,
                        type = ActivityType.FollowedUser.type,
                        parentId = ""
                    )
                )
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = true
                    )
                )
            }else{
                call.respond(
                    HttpStatusCode.BadRequest,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
            }
        }
    }
}


fun Route.unfollowUser(
   followService: FollowService
){
    authenticate {
        delete("/api/following/unfollow"){
            val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val didUserExists = followService.unfollowUserIfExists(request, call.userId)
            if(didUserExists){
                call.respond(
                    HttpStatusCode.BadRequest,
                    BasicApiResponse<Unit>(
                        successful = true
                    )
                )
            }else{
                call.respond(
                    HttpStatusCode.BadRequest,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
            }
        }
    }
}