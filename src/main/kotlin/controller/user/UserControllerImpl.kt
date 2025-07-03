package pw.coding.controller.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import pw.coding.data.models.User

class UserControllerImpl(
    db : CoroutineDatabase
): UserController {

    private val users = db.getCollection<User>()
    override suspend fun createUser(user: User) {
        users.insertOne(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.findOneById(id)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User :: email eq email)
    }
}