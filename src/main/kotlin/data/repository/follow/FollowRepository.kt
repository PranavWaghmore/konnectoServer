package data.repository.follow

import pw.coding.data.models.Following

interface FollowRepository {

    suspend fun followUserIfExists(
        followingUserId : String,
        followedUserId : String
    ): Boolean

    suspend fun unfollowUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun doesUserFollow(followingUserId: String , followedUserId: String):Boolean

    suspend fun getFollowsByUser(userId: String):List<Following>
}