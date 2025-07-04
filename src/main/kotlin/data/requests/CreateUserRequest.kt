package pw.coding.data.requests

data class CreateUserRequest(
    val email: String,
    val username: String,
    val password: String
)
