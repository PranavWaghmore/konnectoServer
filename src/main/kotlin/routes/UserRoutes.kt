package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.models.User
import pw.coding.data.requests.CreateUserRequest
import pw.coding.data.requests.LoginRequest
import pw.coding.data.responses.BasicApiResponse
import pw.coding.util.ApiResponseMessages.FIELDS_BLANK
import pw.coding.util.ApiResponseMessages.INVALID_CREDENTIALS
import pw.coding.util.ApiResponseMessages.USER_ALREADY_EXISTS

fun Route.createUserRoute(
    userRepository: UserRepository
) {
    post("/api/user/create") {
        val request = call.receiveNullable<CreateUserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val userExist = userRepository.getUserByEmail(request.email) != null
        if (userExist) {
            call.respond(
                BasicApiResponse(
                    successful = false,
                    message = USER_ALREADY_EXISTS
                )
            )
            return@post
        }
        if (request.email.isBlank() || request.password.isBlank()
            || request.username.isBlank()
        ) {
            BasicApiResponse(
                successful = false,
                message = FIELDS_BLANK
            )
            return@post
        }
        userRepository.createUser(
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

fun Route.loginUser(
    userRepository: UserRepository
) {
    post("/api/user/login") {
        val request = call.receiveNullable<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if(request.email.isBlank() || request.password.isBlank()){
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val isCorrectPassword = userRepository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )

        if(isCorrectPassword){
            call.respond(
                BasicApiResponse(
                    successful = true
                )
            )
        }else{
            call.respond(
                BasicApiResponse(
                    successful = false,
                    message = INVALID_CREDENTIALS
                )
            )
        }
    }
}