package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import pw.coding.controller.user.UserController
import pw.coding.data.models.User
import pw.coding.data.requests.CreateAccountRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.util.ApiResponseMessages.FIELDS_BLANK
import pw.coding.util.ApiResponseMessages.USER_ALREADY_EXISTS

fun Route.userRoutes() {
    val userController: UserController by inject()
    route("/api/user/create") {
        post {
            val request = call.receiveNullable<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExist = userController.getUserByEmail(request.email) != null
            if(userExist){
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message =USER_ALREADY_EXISTS
                    )
                )
                return@post
            }
            if(request.email.isBlank() || request.password.isBlank()
                || request.username.isBlank()){
                BasicApiResponse(
                    successful = false,
                    message = FIELDS_BLANK
                )
                return@post
            }
            userController.createUser(
                User(
                    email = request.email,
                    username = request.username,
                    password = request.password,
                    profileImageUrl = "",
                    bio = "",
                    gitHubUrl = "",
                    instagramUrl = "",
                    linkedInUrl = "",
                )
            )
            call.respond(BasicApiResponse(successful = true))
        }
    }
}
