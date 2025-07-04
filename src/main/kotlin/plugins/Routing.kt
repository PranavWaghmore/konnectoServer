package pw.coding.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pw.coding.data.repository.user.UserRepository
import pw.coding.routes.createUserRoute
import pw.coding.routes.loginUser

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    routing {
        createUserRoute(userRepository = userRepository)
        loginUser(userRepository = userRepository)
    }
}
