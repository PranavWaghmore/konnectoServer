package pw.coding.service

import data.repository.likes.LikeRepository

class LikeService(
    private val repository: LikeRepository
) {
    suspend fun likeParent(userId : String , parentId: String) : Boolean{
        return repository.likeParent(userId, parentId)
    }
    suspend fun unlinkParent(userId: String , parentId: String) : Boolean{
        return repository.unLikeParent(userId, parentId)
    }

    suspend fun deleteLikesForParent(parentId: String){
        repository.deleteLikesForParent(parentId)
    }
}