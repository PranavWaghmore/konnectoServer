package pw.coding.plugins

import data.repository.follow.FollowRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.repository.user.UserRepository
import pw.coding.routes.*
import pw.coding.service.FollowService
import pw.coding.service.UserService

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val userService: UserService by inject()
    val followRepository : FollowRepository by inject()
    val followService: FollowService by inject()
    val postRepository : PostRepository by inject()
    routing {
        // User routes
        createUserRoute(userService)
        loginUser(userRepository = userRepository)

        // Following routes
        followUser(followService)
        unfollowUser(followService)

        // Post
        createPostRoute(postRepository)
    }
}
