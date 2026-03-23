package data.repository.likes

import pw.coding.data.models.Like
import pw.coding.data.util.ParentType
import pw.coding.util.Constants

interface LikeRepository {

    suspend fun likeParent(userId : String, parentId : String , parentType: Int): Boolean

    suspend fun unLikeParent(userId: String, parentId: String, parentType: Int): Boolean

    suspend fun deleteLikesForParent(parentId: String)

    suspend fun getLikesForParent(
        parentId: String,
        page: Int = 0,
        pageSize: Int = Constants.ACTIVITY_PAGE_SIZE
    ): List<Like>

    suspend fun getLikedParentIdsByUser(
        userId: String,
        parentIds: List<String>
    ): List<String>

    suspend fun isLikedParent(userId: String,parentId: String): Boolean
}