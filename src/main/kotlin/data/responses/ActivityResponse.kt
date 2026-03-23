package pw.coding.data.responses

data class ActivityResponse(
    val userId: String,
    val parentId: String,
    val username : String,
    val type: Int,
    val timeStamp: Long,
    val id : String,
)
