package pw.coding.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.util.idValue
import pw.coding.data.models.User
import pw.coding.data.requests.CreateUserRequest
import pw.coding.data.requests.LoginRequest
import pw.coding.data.responses.AuthResponse
import pw.coding.data.responses.BasicApiResponse
import pw.coding.service.UserService
import pw.coding.util.ApiResponseMessages.FIELDS_BLANK
import pw.coding.util.ApiResponseMessages.INVALID_CREDENTIALS
import pw.coding.util.ApiResponseMessages.USER_ALREADY_EXISTS
import pw.coding.util.QueryParams
import java.util.*

fun Route.createUser(
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
   userService: UserService,
   jwtIssuer: String,
   jwtAudience: String,
   jwtSecret : String
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
        val user = userService.getUserByEmail(request.email) ?: kotlin.run {
            call.respond(
                BasicApiResponse(
                    successful = false,
                    message = INVALID_CREDENTIALS
                )
            )
            return@post
        }
        val isCorrectPassword = userService.validatePassword(
            enteredPassword = request.password,
            actualPassword = user.password
        )
        if(isCorrectPassword){
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withClaim("userId",user.id)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() +expiresIn))
                .withAudience(jwtAudience)
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                AuthResponse(
                    token = token
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


fun Route.searchUser(userService: UserService){
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if(query.isNullOrBlank()){
                call.respond(
                    HttpStatusCode.OK,
                    listOf<User>()
                )
                return@get
            }

            val searchUsers = userService.searchForUsers(query, call.userID)
            call.respond(
                HttpStatusCode.OK,
                searchUsers
            )
        }
    }
}