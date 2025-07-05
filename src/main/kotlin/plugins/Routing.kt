package pw.coding.plugins

import data.repository.follow.FollowRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pw.coding.data.repository.user.UserRepository
import pw.coding.routes.createUserRoute
import pw.coding.routes.followUser
import pw.coding.routes.loginUser
import pw.coding.routes.unfollowUser

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository : FollowRepository by inject()
    routing {
        // User routes
        createUserRoute(userRepository = userRepository)
        loginUser(userRepository = userRepository)

        // Following routes

        followUser(followRepository)
        unfollowUser(followRepository)
    }
}
