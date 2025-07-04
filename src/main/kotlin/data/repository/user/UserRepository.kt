package pw.coding.data.repository.user

import pw.coding.data.models.User

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String) : User?

    suspend fun getUserByEmail(email: String) : User?

    suspend fun doesPasswordForUserMatch(email: String , enteredPassword: String): Boolean
}