package pw.coding.plugins

import data.repository.follow.FollowRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.repository.user.UserRepository
import pw.coding.routes.*

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository : FollowRepository by inject()
    val postRepository : PostRepository by inject()
    routing {
        // User routes
        createUserRoute(userRepository = userRepository)
        loginUser(userRepository = userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)

        // Post
        createPostRoute(postRepository)
    }
}
