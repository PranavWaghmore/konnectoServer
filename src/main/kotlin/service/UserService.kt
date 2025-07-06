package pw.coding.service

import pw.coding.data.models.User
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.requests.CreateUserRequest
import pw.coding.data.requests.LoginRequest
import kotlin.reflect.jvm.internal.ReflectProperties.Val

class UserService(
    private val repository: UserRepository
) {

    suspend fun doesUserWithEmailExist(email : String): Boolean{
        return repository.getUserByEmail(email) != null
    }

    suspend fun createUser(request: CreateUserRequest){
        repository.createUser(
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
    }

    fun validateCreateAccountRequest(request: CreateUserRequest): ValidationEvent{
        return if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()) {
            ValidationEvent.ErrorFieldEmpty
        }else{
            ValidationEvent.Success
        }
    }

    sealed class ValidationEvent(){
        object ErrorFieldEmpty: ValidationEvent()
        object Success: ValidationEvent()
    }

//    fun validateLoginAccountRequest(request: LoginRequest):ValidationEvent{
//        return if (request.email.isBlank() || request.password.isBlank()) {
//            ValidationEvent.ErrorFieldEmpty
//        }else{
//            ValidationEvent.Success
//        }
//    }

    suspend fun isLoginPasswordCorrect(request: LoginRequest):Boolean{
        return repository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
    }
}