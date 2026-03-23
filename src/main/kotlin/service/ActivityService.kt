package pw.coding.service

import pw.coding.data.models.Activity
import pw.coding.data.repository.activity.ActivityRepository
import pw.coding.data.repository.comment.CommentRepository
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.responses.ActivityResponse
import pw.coding.data.util.ActivityType
import pw.coding.data.util.ParentType

class ActivityService(
    private val activityRepository: ActivityRepository,
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository

    ) {
    suspend fun getActivitiesForUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<ActivityResponse> {
        return activityRepository.getActivitiesForUser(userId, page, pageSize)
    }

    suspend fun addCommentActivity(
        byUserId: String,
        postId: String
    ):Boolean{

        val userIdOfPost = postRepository.getPost(postId)?.userId ?: return false
        if(byUserId == userIdOfPost) return false
        activityRepository.createActivity(
            Activity(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = userIdOfPost,
                parentId = postId,
                type = ActivityType.CommentedOnPost.type
            )
        )
        return true
    }
    suspend fun addLikeActivity(
        byUserId: String,
        parentType: ParentType,
        parentID: String
    ): Boolean{
        val toUserID = when(parentType){
            is ParentType.Post ->
                postRepository.getPost(parentID)?.userId
            is ParentType.Comment ->
                commentRepository.getComment(parentID)?.userId
        }?:return false

        if(byUserId == toUserID) return false

        activityRepository.createActivity(
            Activity(
                timestamp = System.currentTimeMillis(),
                byUserId = byUserId,
                toUserId = toUserID,
                type = when(parentType){
                    is ParentType.Post -> ActivityType.LikedPost.type
                    is ParentType.Comment -> ActivityType.LikedComment.type
                },
                parentId = parentID
            )
        )
        return true
    }

    suspend fun createActivity(activity: Activity) {
        activityRepository.createActivity(activity)
    }

    suspend fun deleteActivity(activityId: String): Boolean {
        return activityRepository.deleteActivity(activityId)
    }
}