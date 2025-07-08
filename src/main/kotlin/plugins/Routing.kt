package pw.coding.plugins

import com.auth0.jwt.JWT
import data.repository.follow.FollowRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.repository.user.UserRepository
import pw.coding.routes.*
import pw.coding.service.FollowService
import pw.coding.service.PostService
import pw.coding.service.UserService

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        createUserRoute(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        // Following routes
        followUser(followService)
        unfollowUser(followService)

        // Post
        createPostRoute(postService, userService)
    }
}
