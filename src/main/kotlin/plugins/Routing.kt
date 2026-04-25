package pw.coding.plugins

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.sessions
import org.koin.ktor.ext.inject
import pw.coding.routes.*
import pw.coding.service.*
import pw.coding.service.chat.ChatController
import pw.coding.service.chat.ChatService
import pw.coding.service.chat.ChatSession

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()
    val skillsService: SkillsService by inject()
    val chatService: ChatService by inject()
    val chatController: ChatController by inject()
    val gson: Gson by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        authenticate()
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
        getPost(postService)

        //Like
        likeParent(likeService, activityService)
        unlikeParent(likeService)
        getUsersWhoLikedParent(likeService)

        //Comment Route
        createComment(commentService, activityService)
        getCommentsForPost(commentService)
        deleteComment(commentService, likeService)

        //Activity
        getActivitiesForUser(activityService)

        //Skills
        getSkills(skillsService)


        //Chat
        getMessagesForChat(chatService)
        getChatsForUser(chatService)
        chatWebSocket(chatController = chatController, gson)

        static {
            resources("static")
        }
    }
}
