package pw.coding.service

import data.repository.likes.LikeRepository
import pw.coding.data.models.Comment
import pw.coding.data.repository.comment.CommentRepository
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.requests.CreateCommentRequest
import pw.coding.data.responses.CommentDto
import pw.coding.util.Constants

class CommentService(
    val commentRepository: CommentRepository,
    val userRepository: UserRepository,
    val likeRepository: LikeRepository
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
        commentRepository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                postId = createCommentRequest.postId,
                userId = userId,
                timestamp = System.currentTimeMillis(),
                likeCount = 0
            )
        )
        return ValidationEvent.Success
    }

    suspend fun getCommentsForPost(postId: String, ownUserId: String):List<CommentDto>{
        val comments =  commentRepository.getCommentsForPost(postId)
        if (comments.isEmpty()) return emptyList()
        val users = userRepository.getUsers(comments.map { it.userId }).distinct()

        val userMap = users.associateBy { it.id }

        val likedCommentIds = likeRepository.getLikedParentIdsByUser(
            userId = ownUserId,
            parentIds = comments.map { it.id }
        ).toSet()

        return comments.map { comment ->
            val user = userMap[comment.userId]

            CommentDto(
                id = comment.id,
                username = user?.username ?: "",
                profilePictureUrl = user?.profileImageUrl ?: "",
                timestamp = comment.timestamp,
                comment = comment.comment,
                isLiked = comment.id in likedCommentIds,
                likeCount = comment.likeCount
            )
        }
    }

    suspend fun deleteComment(commentId: String): Boolean{
        return commentRepository.deleteComment(commentId)
    }

    suspend fun getCommentById(commentId: String):Comment?{
        return commentRepository.getComment(commentId)
    }

    suspend fun deleteCommentsForPost(postId: String){
        commentRepository.deleteCommentsFromPost(postId)
    }
    sealed class ValidationEvent{
        object ErrorFieldEmpty: ValidationEvent()
        object ErrorCommentTooLong: ValidationEvent()
        object Success : ValidationEvent()
    }
}