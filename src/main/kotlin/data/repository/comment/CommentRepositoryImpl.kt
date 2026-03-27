package pw.coding.data.repository.comment

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.inc
import org.litote.kmongo.setValue
import pw.coding.data.models.Comment
import pw.coding.data.models.Post
import pw.coding.data.responses.CommentDto

class CommentRepositoryImpl(
    db: CoroutineDatabase
): CommentRepository {

    private val comments = db.getCollection<Comment>()
    private val posts = db.getCollection<Post>()
    override suspend fun createComment(comment: Comment):String {
        comments.insertOne(comment)

        val updateResult = posts.updateOne(
            Post::id eq comment.postId,
            inc(Post::commentCount, 1)
        )

        if (updateResult.modifiedCount == 0L) {
            throw IllegalStateException("Failed to update post comment count")
        }
        
        return comment.id
    }

    override suspend fun deleteComment(commentId: String): Boolean {
        val deleteCount = comments.deleteOneById(commentId).deletedCount
        return deleteCount > 0
    }

    override suspend fun deleteCommentsFromPost(postId: String): Boolean {
        return comments.deleteMany(
            Comment::postId eq postId
        ).wasAcknowledged()
        
    }

    override suspend fun getCommentsForPost(postId: String) : List<Comment> {
        return comments.find(Comment::postId eq postId).toList()
    }

    override suspend fun getComment(commentId: String) :Comment? {
        return comments.findOneById(commentId)
    }
}