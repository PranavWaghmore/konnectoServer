package data.repository.likes

import pw.coding.data.models.Like
import pw.coding.util.Constants

interface LikeRepository {

    suspend fun likeParent(userId : String, parentId : String , parentType: Int): Boolean

    suspend fun unLikeParent(userId: String, parentId: String): Boolean

    suspend fun deleteLikesForParent(parentId: String)

    suspend fun getLikesForParent(
        parentId: String,
        page: Int = 0,
        pageSize: Int = Constants.ACTIVITY_PAGE_SIZE
    ): List<Like>
}