package pw.coding.plugins

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pw.coding.routes.*
import pw.coding.service.*

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()
    val gson: Gson by inject()

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
        searchUser(userService)
        getUserProfile(userService)
        getPostsForProfile(postService)
        updateUserProfile(userService , gson)

        // Following routes
        followUser(followService , activityService)
        unfollowUser(followService)

        // Post
        createPost(postService, gson)
        getPostsForFollows(postService)
        deletePost(postService, likeService , commentService)

        //Like
        likeParent(likeService, activityService)
        unlikeParent(likeService)

        //Comment Route
        createComment(commentService, activityService)
        getCommentsForPost(commentService)
        deleteComment(commentService, likeService)

        //Activity
        getActivitiesForUser(activityService)

        //staticResources("/static", "static")
        static {
            resources("static")
        }
    }
}
