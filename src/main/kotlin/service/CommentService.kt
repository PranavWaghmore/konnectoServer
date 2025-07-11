package pw.coding.service

import pw.coding.data.models.Comment
import pw.coding.data.repository.comment.CommentRepository
import pw.coding.data.requests.CreateCommentRequest
import pw.coding.util.Constants
import kotlin.time.Clock

class CommentService(
    val repository: CommentRepository
) {

    suspend fun createComment(createCommentRequest: CreateCommentRequest , userId: String): ValidationEvent{

        createCommentRequest.apply {
            if(comment.isBlank() ||  postId.isBlank()){
                return ValidationEvent.ErrorFieldEmpty
            }

            if(comment.length> Constants.MAX_COMMENT_LENGTH){
                return ValidationEvent.ErrorCommentTooLong
            }
        }
        repository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                postId = createCommentRequest.postId,
                userId = userId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    suspend fun getCommentsForPost(postId: String):List<Comment>{
        return repository.getCommentsForPost(postId)
    }

    suspend fun deleteComment(commentId: String): Boolean{
        return repository.deleteComment(commentId)
    }

    suspend fun getCommentById(commentId: String):Comment?{
        return repository.getComment(commentId)
    }
    sealed class ValidationEvent{
        object Success: ValidationEvent()
        object ErrorFieldEmpty: ValidationEvent()
        object ErrorCommentTooLong: ValidationEvent()
    }
}