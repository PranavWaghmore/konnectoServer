package pw.coding.data.repository.user

import pw.coding.data.models.User
import pw.coding.data.requests.UpdateProfileRequest

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String) : User?

    suspend fun getUserByEmail(email: String) : User?

    suspend fun updateUser(
        userId: String,
        profileImageUrl: String?,
        bannerUrl: String?,
        updateProfileRequest: UpdateProfileRequest
    ):Boolean

    suspend fun doesPasswordForUserMatch(email: String , enteredPassword: String): Boolean

    suspend fun doesUserBelongsToUserId(email: String , userId: String): Boolean

    suspend fun searchForUsers(query: String):List<User>

    suspend fun getUsers(userIds: List<String>): List<User>
}