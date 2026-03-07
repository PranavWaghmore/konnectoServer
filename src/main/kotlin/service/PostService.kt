package pw.coding.service

import pw.coding.data.models.Post
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.requests.CreatePostRequest
import pw.coding.data.responses.PostResponse
import pw.coding.data.responses.ProfileResponse
import pw.coding.util.Constants

class PostService(
    private  val postRepository: PostRepository,
    private  val userRepository: UserRepository,
) {
    suspend fun createPost(
        request: CreatePostRequest,
        userId: String,
        imageUrl: String): Boolean{
       return postRepository.createPost(
            post = Post(
                imageUrl = imageUrl,
                userId = userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
       )
    }

    suspend fun getPostsByFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.POST_PAGE_SIZE
    ): List<PostResponse> {
        val posts =  postRepository.getPostsByFollows(userId , page , pageSize)
        val userIds = posts.map { it.userId }.distinct()
        val users = userRepository.getUsers(userIds)
        val usersById = users.associateBy { it.id }

        return posts.map { post ->
            val user = usersById[post.userId]
            PostResponse(
                userId = post.userId,
                imageUrl = post.imageUrl,
                username = user?.username ?: "Hardcoded Username Ktor",
                profilePictureUrl = user?.profileImageUrl ?: "Hardcoded url ktor",
                timestamp = post.timestamp,
                description = post.description,
                likeCount = post.likeCount,
                commentCount = post.commentCount
            )
        }
    }

    suspend fun getPostsForProfile(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.POST_PAGE_SIZE
    ): List<PostResponse> {
        val posts =  postRepository.getPostsForProfile(userId , page , pageSize)
        val user = userRepository.getUserById(userId)
        return posts.map { post ->
            PostResponse(
                userId = post.userId,
                imageUrl = post.imageUrl,
                username = user?.username ?: "Hardcoded Username Ktor",
                profilePictureUrl = user?.profileImageUrl ?: "Hardcoded url ktor",
                timestamp = post.timestamp,
                description = post.description,
                likeCount = post.likeCount,
                commentCount = post.commentCount
            )
        }
    }

    suspend fun getPost(postId : String) : Post?{
        return postRepository.getPost(postId)
    }

    suspend fun deletePost(postId: String){
        postRepository.deletePostById(postId)
    }
}