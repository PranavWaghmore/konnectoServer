package pw.coding.di

import com.google.gson.Gson
import data.repository.follow.FollowRepository
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import pw.coding.data.repository.follow.FollowRepositoryImpl
import data.repository.likes.LikeRepository
import pw.coding.data.repository.activity.ActivityRepository
import pw.coding.data.repository.activity.ActivityRepositoryImpl
import pw.coding.data.repository.comment.CommentRepository
import pw.coding.data.repository.comment.CommentRepositoryImpl
import data.repository.likes.LikeRepositoryImpl
import pw.coding.data.repository.post.PostRepository
import pw.coding.data.repository.post.PostRepositoryImpl
import pw.coding.data.repository.skill.SkillRepository
import pw.coding.data.repository.skill.SkillRepositoryImpl
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.repository.user.UserRepositoryImpl
import pw.coding.service.*
import pw.coding.util.Constants


val mainModule = module{
    single {
        val client = KMongo.createClient(
            connectionString = "mongodb+srv://pranavwaghmore1801:${System.getenv("MONGO_PW")}@cluster0.q4dhya8.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        ).coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserRepository>{ UserRepositoryImpl(get()) }
    single<FollowRepository>{ FollowRepositoryImpl(get()) }
    single<PostRepository> { PostRepositoryImpl(get()) }
    single<LikeRepository> { LikeRepositoryImpl(get()) }
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single<ActivityRepository> { ActivityRepositoryImpl(get()) }
    single<SkillRepository> { SkillRepositoryImpl(get()) }

    single { UserService(get(),get()) }
    single { FollowService(get()) }
    single { PostService(get(),get()) }
    single { LikeService(get(),get(),get()) }
    single { CommentService(get(),get(),get()) }
    single { ActivityService(get(),get(),get()) }
    single { SkillsService(get()) }

    single { Gson() }

}