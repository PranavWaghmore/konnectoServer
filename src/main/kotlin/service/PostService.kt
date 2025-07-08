package pw.coding.service

import pw.coding.data.models.Post
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.requests.CreatePostRequest

class PostService(
    private  val repository: PostRepository
) {
    suspend fun createPostIfUserExists(request: CreatePostRequest): Boolean{
       return repository.createPostIfUserExists(
            post = Post(
                imageUrl = "",
                userId = request.userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
       )
    }
}