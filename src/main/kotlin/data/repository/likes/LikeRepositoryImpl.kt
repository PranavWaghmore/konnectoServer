package data.repository.likes

import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue
import pw.coding.data.models.Comment
import pw.coding.data.models.Like
import pw.coding.data.models.Post
import pw.coding.data.models.User
import pw.coding.data.util.ParentType

class LikeRepositoryImpl(
    db : CoroutineDatabase
): LikeRepository {
    private val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()
    private val posts = db.getCollection<Post>()
    private val comments = db.getCollection<Comment>()

    override suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean {
        val doesUserExists = users.findOneById(userId) != null
        val type = ParentType.fromType(parentType)
        return if(doesUserExists){
            when(type){
                is ParentType.Post -> {
                    val post = posts.findOneById(parentId) ?: return false
                    posts.updateOneById(
                        id = parentId,
                        update = setValue(Post::likeCount, post.likeCount + 1)
                    )
                }
                is ParentType.Comment -> {
                    val comment = comments.findOneById(parentId) ?: return false
                    comments.updateOneById(
                        id = parentId,
                        update = setValue(Comment::likeCount, comment.likeCount + 1)
                    )
                }
            }
            likes.insertOne(
                Like(
                    userId, parentId, parentType, System.currentTimeMillis()
                )
            )
            true
        }else{
            false
        }
    }

    override suspend fun unLikeParent(userId: String, parentId: String, parentType: Int): Boolean {
        val doesUserExists = users.findOneById(userId) != null
        val type = ParentType.fromType(parentType)
        return if(doesUserExists){
            when(type){
                is ParentType.Post -> {
                    val post = posts.findOneById(parentId) ?: return false
                    posts.updateOneById(
                        id = parentId,
                        update = setValue(Post::likeCount, post.likeCount - 1)
                    )
                }
                is ParentType.Comment -> {
                    val comment = comments.findOneById(parentId) ?: return false
                    comments.updateOneById(
                        id = parentId,
                        update = setValue(Comment::likeCount, comment.likeCount - 1)
                    )
                }
            }
            likes.deleteOne(
                and(
                    Like::userId eq userId,
                    Like::parentId eq parentId
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

    override suspend fun isLikedParent(userId: String, parentId: String): Boolean {
        return likes.findOne(
            and(
                Like::userId eq userId,
                Like::parentId eq parentId
            )
        ) != null
    }


}