package pw.coding.service

import data.repository.follow.FollowRepository
import pw.coding.data.requests.FollowUpdateRequest

class FollowService(
    private val followRepository: FollowRepository
) {
    suspend fun followUserIfExists(request: FollowUpdateRequest):Boolean{
        return followRepository.followUserIfExists(
            request.followingUserId,
            request.followedUserId
        )
    }
    suspend fun unfollowUserIfExists(request: FollowUpdateRequest):Boolean{
        return followRepository.followUserIfExists(
            request.followingUserId,
            request.followedUserId
        )
    }
}