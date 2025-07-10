package pw.coding.data.requests

data class CreateCommentRequest(
    val comment: String,
    val userId: String,
    val postId: String
)
