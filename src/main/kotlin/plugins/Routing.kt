package pw.coding.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pw.coding.routes.*
import pw.coding.service.FollowService
import pw.coding.service.LikeService
import pw.coding.service.PostService
import pw.coding.service.UserService

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        createUser(userService)
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
        createPost(postService, userService)
        getPostForFollows(postService , userService)
        deletePost(postService,userService , likeService)

        //like
        likeParent(likeService, userService)
        unlikeParent(likeService , userService)
    }
}
