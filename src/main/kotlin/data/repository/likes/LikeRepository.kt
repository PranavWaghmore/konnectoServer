package data.repository.likes

import pw.coding.data.util.ParentType

interface LikeRepository {

    suspend fun likeParent(userId : String, parentId : String , parentType: Int): Boolean

    suspend fun unLikeParent(userId: String, parentId: String): Boolean

    suspend fun deleteLikesForParent(parentId: String)
}