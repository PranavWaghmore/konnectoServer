package pw.coding.service

import pw.coding.data.models.Comment
import pw.coding.data.repository.comment.CommentRepository
import pw.coding.data.requests.CreateCommentRequest
import pw.coding.util.Constants
import kotlin.time.Clock

class CommentService(
    val repository: CommentRepository
) {

    suspend fun createComment(createCommentRequest: CreateCommentRequest): ValidationEvent{

        createCommentRequest.apply {
            if(comment.isBlank() || userId.isBlank() || postId.isBlank()){
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
                userId = createCommentRequest.userId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    sealed class ValidationEvent{
        object Success: ValidationEvent()
        object ErrorFieldEmpty: ValidationEvent()
        object ErrorCommentTooLong: ValidationEvent()
    }
}