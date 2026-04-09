package pw.coding

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import pw.coding.di.mainModule
import pw.coding.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    configureSecurity()
    configureSockets()
    configureSessions()
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSerialization()

    println("Ktor app started")
}


