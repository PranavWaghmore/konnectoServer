package pw.coding.di

import data.repository.follow.FollowRepository
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import pw.coding.data.repository.follow.FollowRepositoryImpl
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.repository.user.UserRepositoryImpl
import pw.coding.service.FollowService
import pw.coding.service.UserService
import pw.coding.util.Constants


val mainModule = module{
    single {
        val client = KMongo.createClient(
            connectionString = "mongodb+srv://pranavwaghmore1801:${System.getenv("MONGO_PW")}@cluster0.q4dhya8.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        ).coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserRepository>{
        UserRepositoryImpl(get())
    }
    single<FollowRepository>{
        FollowRepositoryImpl(get())
    }

    single {
        UserService(get())
    }
    single {
        FollowService(get())
    }
}