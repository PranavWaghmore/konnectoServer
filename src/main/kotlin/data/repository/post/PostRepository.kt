package pw.coding.data.repository.post

import pw.coding.data.models.Post
import pw.coding.util.Constants

interface PostRepository {

    suspend fun createPost(post: Post):Boolean

    suspend fun deletePostById(postId : String)

    suspend fun getPostsByFollows(
        userId: String,
        page: Int= 0,
        pageSize: Int = Constants.POST_PAGE_SIZE
    ): List<Post>

    suspend fun getPostsForProfile(
        userId: String,
        page: Int= 0,
        pageSize: Int = Constants.POST_PAGE_SIZE
    ): List<Post>

    suspend fun getPost(postId: String) : Post?
}