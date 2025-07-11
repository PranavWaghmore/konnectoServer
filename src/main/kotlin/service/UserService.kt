package pw.coding.service

import pw.coding.data.models.User
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.requests.CreateUserRequest
import pw.coding.data.requests.LoginRequest

class UserService(
    private val repository: UserRepository
) {

    suspend fun doesUserWithEmailExist(email : String): Boolean{
        return repository.getUserByEmail(email) != null
    }

    suspend fun doesEmailBelongsToUserId(email: String , userId : String): Boolean{
        return  repository.doesUserBelongsToUserId(email, userId)
    }

    suspend fun getUserByEmail(email: String):User?{
        return repository.getUserByEmail(email)
    }

    suspend fun validatePassword(enteredPassword: String , actualPassword : String):Boolean{
        return enteredPassword == actualPassword
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

    suspend fun doesPasswordMatchForUser(request: LoginRequest):Boolean{
        return repository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
    }
}