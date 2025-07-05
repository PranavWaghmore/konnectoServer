package pw.coding.data.requests

data class FollowUpdateRequest(
    val followingUserId : String,
    val followedUserId : String,
    val isFollowing : Boolean
)
