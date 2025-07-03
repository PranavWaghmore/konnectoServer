package pw.coding.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Skills(
    @BsonId
    val id: String = ObjectId().toString(),
    val skill: String,
    val iconUrl: String
)
