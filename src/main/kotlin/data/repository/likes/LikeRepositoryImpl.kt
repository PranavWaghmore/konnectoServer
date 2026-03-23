package data.repository.likes

import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import pw.coding.data.models.Like
import pw.coding.data.models.User

class LikeRepositoryImpl(
    db : CoroutineDatabase
): LikeRepository {
    private val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()

    override suspend fun likeParent(userId: String, parentId: String , parentType: Int): Boolean {
        val doesUserExists = users.findOneById(userId) != null
        return if(doesUserExists){
            likes.insertOne(Like(userId, parentId, parentType, System.currentTimeMillis()))
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

    override suspend fun getLikesForParent(
        parentId: String,
        page: Int,
        pageSize: Int
    ): List<Like> {
        return likes
            .find(Like::parentId eq parentId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Like::timestamp)
            .toList()
    }

    override suspend fun getLikedParentIdsByUser(
        userId: String,
        parentIds: List<String>
    ): List<String> {
        return likes.find(
            and(
                Like::userId eq userId,
                Like::parentId `in` parentIds
            )
        ).toList().map { like ->
            like.parentId
        }
    }

}