package pw.coding.service

import data.repository.follow.FollowRepository
import pw.coding.data.models.User
import pw.coding.data.repository.user.UserRepository
import pw.coding.data.requests.CreateAccountRequest
import pw.coding.data.requests.LoginRequest
import pw.coding.data.requests.UpdateProfileRequest
import pw.coding.data.responses.ProfileResponse
import pw.coding.data.responses.UserResponseItem

class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {

    suspend fun doesUserWithEmailExist(email : String): Boolean{
        return userRepository.getUserByEmail(email) != null
    }

    suspend fun getUserProfile(
        userId: String,
        callerUserId: String
    ): ProfileResponse? {
        val user = userRepository.getUserById(userId) ?: return  null
        return ProfileResponse(
            userId = userId,
            username = user.username,
            bio = user.bio,
            followerCount = user.followerCount,
            followingCount = user.followingCount,
            postCount = user.postCount,
            profilePictureUrl = user.profileImageUrl,
            bannerUrl = user.bannerUrl,
            topSkills = user.skills,
            gitHubUrl = user.gitHubUrl,
            instagramUrl = user.instagramUrl,
            linkedInUrl = user.linkedInUrl,
            isOwnProfile = (userId == callerUserId),
            isFollowing = if(userId != callerUserId){
                followRepository.doesUserFollow(callerUserId, userId)
            }else{
                 false
            }
        )
    }

    suspend fun  updateUser(
        userId: String,
        profileImageUrl: String?,
        bannerUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ):Boolean{
        return userRepository.updateUser(userId, profileImageUrl, bannerUrl,updateProfileRequest)
    }
    suspend fun searchForUsers(query: String , userId: String):List<UserResponseItem>{
        val users = userRepository.searchForUsers(query)
        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null
            UserResponseItem(
                userId= user.id,
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
    suspend fun createUser(request: CreateAccountRequest){
        userRepository.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = "",
                bannerUrl = "",
                bio = "",
                gitHubUrl = "",
                instagramUrl = "",
                linkedInUrl = "",
            )
        )
    }

    fun validateCreateAccountRequest(request: CreateAccountRequest): ValidationEvent{
        return if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()) {
            ValidationEvent.ErrorFieldEmpty
        }else{
            ValidationEvent.Success
        }
    }

    sealed class ValidationEvent{
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