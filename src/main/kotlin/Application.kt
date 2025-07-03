package pw.coding

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import pw.coding.di.mainModule
import pw.coding.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
    install(Koin){
        modules(mainModule)
    }
}

