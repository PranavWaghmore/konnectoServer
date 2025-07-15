package pw.coding.service

import data.repository.follow.FollowRepository
import pw.coding.data.models.User
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.requests.CreateUserRequest
import pw.coding.data.requests.LoginRequest
import pw.coding.data.responses.UserResponseItem

class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {

    suspend fun doesUserWithEmailExist(email : String): Boolean{
        return userRepository.getUserByEmail(email) != null
    }

    suspend fun searchForUsers(query: String , userId: String):List<UserResponseItem>{
        val users = userRepository.searchForUsers(query)
        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null
            UserResponseItem(
                username = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }
    }

    suspend fun getUserByEmail(email: String):User?{
        return userRepository.getUserByEmail(email)
    }

    fun validatePassword(enteredPassword: String , actualPassword : String):Boolean{
        return enteredPassword == actualPassword
    }
    suspend fun createUser(request: CreateUserRequest){
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
        return userRepository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
    }
}