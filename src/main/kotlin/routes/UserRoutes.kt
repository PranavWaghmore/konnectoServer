package pw.coding.routes

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.models.User
import pw.coding.data.requests.UpdateProfileRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.data.responses.ProfileResponse
import pw.coding.data.responses.UserResponseItem
import pw.coding.service.PostService
import pw.coding.service.UserService
import pw.coding.util.ApiResponseMessages.USER_NOT_FOUND
import pw.coding.util.Constants
import pw.coding.util.Constants.BANNER_IMAGE_PATH
import pw.coding.util.Constants.BASE_URL
import pw.coding.util.Constants.PROFILE_PICTURE_PATH
import pw.coding.util.QueryParams
import pw.coding.util.save
import java.io.File

fun Route.searchUser(userService: UserService) {
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if (query.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.OK,
                    listOf<UserResponseItem>()
                )
                return@get
            }

            val searchUsers = userService.searchForUsers(query, call.userId)
            call.respond(
                HttpStatusCode.OK,
                searchUsers
            )
        }
    }
}

fun Route.getUserProfile(userService: UserService) {
    authenticate {
        get("/api/user/profile") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            if (userId.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest
                )
                return@get
            }
            val profileResponse = userService.getUserProfile(userId, call.userId)
            if (profileResponse == null) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse<Unit>(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
                return@get
            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = true,
                        message = "Profile_Found",
                        data = profileResponse
                    )
                )
            }
        }
    }
}

fun Route.getPostsForProfile(
    postService: PostService
) {
    authenticate {
        get("/api/user/post") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]
                ?.toIntOrNull() ?: Constants.POST_PAGE_SIZE

            val posts = postService.getPostsForProfile(
                userId = userId ?: call.userId,
                page,
                pageSize
            )
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.updateUserProfile(
    userService: UserService,
    gson: Gson
) {
    authenticate {
        put("/api/user/update") {
            val multipart = call.receiveMultipart()
            var updateProfileRequest: UpdateProfileRequest? = null
            var profilePictureFileName: String? = null
            var bannerImageFileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "update_profile_data") {
                            updateProfileRequest = gson.fromJson(
                                partData.value,
                                UpdateProfileRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        if(partData.name == "profile_picture"){
                            profilePictureFileName =  partData.save(PROFILE_PICTURE_PATH)
                        }else if(partData.name == "banner_image"){
                            bannerImageFileName = partData.save(BANNER_IMAGE_PATH)
                        }
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }
            }
            val profilePictureUrl = "${BASE_URL}profile_pictures/$profilePictureFileName"
            val bannerImageUrl = "${BASE_URL}banner_images/$bannerImageFileName"
            updateProfileRequest?.let { request ->
                val updateAcknowledged = userService.updateUser(
                    userId = call.userId,
                    profileImageUrl = profilePictureUrl,
                    bannerUrl = bannerImageUrl,
                    updateProfileRequest = request
                )
                if (updateAcknowledged) {
                    call.respond(HttpStatusCode.OK, BasicApiResponse<Unit>(successful = true))
                } else {
                    File("${PROFILE_PICTURE_PATH}/$profilePictureFileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}