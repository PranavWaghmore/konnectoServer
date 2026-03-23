package pw.coding.service

import data.repository.follow.FollowRepository
import pw.coding.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {
    suspend fun followUserIfExists(request: FollowUpdateRequest , followingUserId: String):Boolean{

        return followRepository.followUserIfExists(
            followingUserId,
            request.followedUserId
        )
    }
    suspend fun unfollowUserIfExists(followedUserId: String , followingUserId: String):Boolean{
        return followRepository.unfollowUserIfExists(
            followingUserId,
            followedUserId
        )
    }
}