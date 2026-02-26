package pw.coding.routes

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.requests.CreatePostRequest
import pw.coding.data.requests.DeletePostRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.CommentService
import pw.coding.service.LikeService
import pw.coding.service.PostService
import pw.coding.util.Constants
import pw.coding.util.Constants.BASE_URL
import pw.coding.util.Constants.POST_PICTURE_PATH
import pw.coding.util.QueryParams
import pw.coding.util.save
import java.io.File

fun Route.createPost(
    postService: PostService,
    gson: Gson,
) {
    authenticate {
        post("/api/post/create") {
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "post_data") {
                            createPostRequest = gson.fromJson(
                                partData.value,
                                CreatePostRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        fileName = partData.save(POST_PICTURE_PATH)
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }
            }
            val postPictureUrl = "${BASE_URL}post_pictures/$fileName"
            createPostRequest?.let { request ->
                val createPostAcknowledged = postService.createPost(
                    userId = call.userId,
                    request = request,
                    imageUrl = postPictureUrl
                )
                if (createPostAcknowledged) {
                    call.respond(HttpStatusCode.OK, BasicApiResponse<Unit>(successful = true))
                } else {
                    File("$POST_PICTURE_PATH/$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
        }
    }
}

fun Route.getPostsForFollows(
    postService: PostService
) {
    authenticate {
        get("/api/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]
                ?.toIntOrNull() ?: Constants.POST_PAGE_SIZE
            val posts = postService.getPostsByFollows(userId = call.userId, page , pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
    commentService: CommentService
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
            if(post.userId == call.userId){
                postService.deletePost(request.postId)
                likeService.deleteLikesForParent(request.postId)
                commentService.deleteCommentsForPost(request.postId)
                call.respond(HttpStatusCode.OK)
            }else{
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}

