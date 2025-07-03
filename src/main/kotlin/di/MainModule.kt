package pw.coding.di

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import pw.coding.controller.user.UserController
import pw.coding.controller.user.UserControllerImpl
import pw.coding.util.Constants


val mainModule = module{
    single {
        val client = KMongo.createClient(
            connectionString = "mongodb+srv://pranavwaghmore1801:" +
                    "${System.getenv("MONGO_PW")}@cluster0.q4dhya8." +
                    "mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        ).coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserController>{
        UserControllerImpl(get())
    }
}