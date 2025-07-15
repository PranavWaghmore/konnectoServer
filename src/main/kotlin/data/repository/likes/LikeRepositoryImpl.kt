package pw.coding.data.repository.like

import data.repository.likes.LikeRepository
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import pw.coding.data.models.Like
import pw.coding.data.models.User
import pw.coding.data.util.ParentType

class LikeRepositoryImpl(
    db : CoroutineDatabase
): LikeRepository {
    private val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()

    override suspend fun likeParent(userId: String, parentId: String , parentType: Int): Boolean {
        val doesUserExists = users.findOneById(userId) != null
        return if(doesUserExists){
            likes.insertOne(Like(userId, parentId , parentType))
            true
        }else{
            false
        }
    }

    override suspend fun unLikeParent(userId: String, parentId: String): Boolean {
        val doesUserExists = users.findOneById(userId) != null
        return if(doesUserExists){
            likes.deleteOne(
                and(
                   Like :: userId eq userId,
                    Like:: parentId eq parentId
                )
            )
            true
        }else{
            false
        }
    }

    override suspend fun deleteLikesForParent(parentId: String) {
        likes.deleteMany(Like::parentId eq parentId)
    }
}