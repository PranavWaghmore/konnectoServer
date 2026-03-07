package pw.coding.data.responses

data class PostResponse(
    val userId: String,
    val imageUrl: String,
    val username : String,
    val profilePictureUrl: String,
    val timestamp: Long,
    val description: String,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
)
