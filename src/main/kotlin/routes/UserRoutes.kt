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
import pw.coding.service.UserService
import pw.coding.util.ApiResponseMessages.FIELDS_BLANK
import pw.coding.util.ApiResponseMessages.INVALID_CREDENTIALS
import pw.coding.util.ApiResponseMessages.USER_ALREADY_EXISTS

fun Route.createUserRoute(
    userService: UserService
) {
    post("/api/user/create") {
        val request = call.receiveNullable<CreateUserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if (userService.doesUserWithEmailExist(request.email)) {
            call.respond(
                BasicApiResponse(
                    successful = false,
                    message = USER_ALREADY_EXISTS
                )
            )
            return@post
        }
        when(userService.validateCreateAccountRequest(request)){
            is UserService.ValidationEvent.ErrorFieldEmpty ->{
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = FIELDS_BLANK
                    ),
                    return@post
                )
            }

            is UserService.ValidationEvent.Success ->{
                userService.createUser(request)
                call.respond(
                    BasicApiResponse(successful = true)
                )
            }
        }
    }
}

fun Route.loginUser(
   userService: UserService
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

        val isCorrectPassword = userService.isLoginPasswordCorrect(request)
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