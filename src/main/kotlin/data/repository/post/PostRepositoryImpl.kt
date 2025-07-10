package pw.coding.data.repository.post

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import pw.coding.data.models.Following
import pw.coding.data.models.Post
import pw.coding.data.models.User

class PostRepositoryImpl(
    db: CoroutineDatabase
) : PostRepository {
    private val posts = db.getCollection<Post>()
    private val following = db.getCollection<Following>()
    private val users = db.getCollection<User>()

    override suspend fun createPostIfUserExists(post: Post): Boolean {
        val doesUserExists = users.findOneById(post.userId) != null
        if (!doesUserExists) {
            return false
        }
        posts.insertOne(post)
        return true
    }

    override suspend fun deletePostById(postId: String) {
        posts.deleteOneById(postId)
    }

    override suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int,
        pageSize: Int,
    ): List<Post> {
        val userIdsFromFollows = following.find(Following::followingUserId eq ownUserId)
            .toList()
            .map {
                it.followedUserId
            }
        return posts.find(Post::userId `in` userIdsFromFollows)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()


    }

    override suspend fun getPost(postId: String): Post? {
        return posts.findOneById(postId)
    }
}