package pw.coding.service

import pw.coding.data.models.Post
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.requests.CreatePostRequest
import pw.coding.util.Constants

class PostService(
    private  val repository: PostRepository
) {
    suspend fun createPostIfUserExists(request: CreatePostRequest , userId: String): Boolean{
       return repository.createPostIfUserExists(
            post = Post(
                imageUrl = "",
                userId = userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
       )
    }

    suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int = 0,
        pageSize: Int = Constants.POST_PAGE_SIZE
    ): List<Post> {
        return repository.getPostsByFollows(ownUserId , page , pageSize)
    }

    suspend fun getPost(postId : String) : Post?{
        return repository.getPost(postId)
    }

    suspend fun deletePost(postId: String){
        repository.deletePostById(postId)
    }
}