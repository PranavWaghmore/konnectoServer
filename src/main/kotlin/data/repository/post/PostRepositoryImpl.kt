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

    override suspend fun createPost(post: Post): Boolean {
        return posts.insertOne(post).wasAcknowledged()
    }

    override suspend fun deletePostById(postId: String) {
        posts.deleteOneById(postId)
    }

    override suspend fun getPostsByFollows(
        userId: String,
        page: Int,
        pageSize: Int,
    ): List<Post> {
        val userIdsFromFollows = following.find(Following::followingUserId eq userId)
            .toList()
            .map { it.followedUserId }

        if(userIdsFromFollows.isEmpty()) return emptyList()

        return posts.find(Post::userId `in` userIdsFromFollows)
            .descendingSort(Post::timestamp)
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
    }

    override suspend fun getPostsForProfile(userId: String, page: Int, pageSize: Int): List<Post> {
        return posts.find(Post::userId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()
    }

    override suspend fun getPost(postId: String): Post? {
        return posts.findOneById(postId)
    }
}