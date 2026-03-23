package pw.coding.data.responses

data class CommentDto(
    val id : String,
    val username: String,
    val profilePictureUrl : String,
    val timestamp: Long,
    val comment : String,
    val isLiked : Boolean,
    val likeCount : Int
)