package pw.coding.data.repository.chat

import org.litote.kmongo.MongoOperator
import org.litote.kmongo.and
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import pw.coding.data.models.Chat
import pw.coding.data.responses.ChatDto
import pw.coding.data.models.Message
import pw.coding.data.models.User

class ChatRepositoryImpl(
    db: CoroutineDatabase
) : ChatRepository {

    private val users = db.getCollection<User>()
    private val messages = db.getCollection<Message>()
    private val chats = db.getCollection<Chat>()

    override suspend fun getMessagesForChat(
        chatId: String,
        page: Int,
        pageSize: Int
    ): List<Message> {
        return messages.find(Message::chatId eq chatId)
            .skip(page * pageSize)
            .limit(pageSize)
            .ascendingSort(Message::timestamp)
            .toList()
    }

    override suspend fun getChatsForUser(ownUserId: String): List<ChatDto> {
        return chats.find(Chat::userIds contains ownUserId)
            .descendingSort(Chat::timestamp)
            .toList()
            .map { chat ->

                val otherUserId = chat.userIds.find { it != ownUserId }

                val user = users.findOneById(otherUserId ?: "")

                val message = messages.findOneById(chat.lastMessageId)

                ChatDto(
                    chatId = chat.id,
                    remoteUserId = user?.id ?: "",
                    remoteUsername = user?.username ?: "Hard",
                    remoteUserProfilePictureUrl = user?.profileImageUrl ?: "",
                    lastMessage = message?.text ?: "Hard",
                    timestamp = message?.timestamp ?: 0
                )
            }
    }

    override suspend fun doesChatBelongToUser(chatId: String, userId: String): Boolean {
        return chats.findOneById(chatId)?.userIds?.any { it == userId } ?: false
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }

    override suspend fun insertChat(userId1: String, userId2: String, messageId: String) {

        val chat = Chat(
                userIds = listOf(
                    userId1,
                    userId2
                ),
                lastMessageId = messageId,
                timestamp = System.currentTimeMillis()
            )
        val chatId = chats.insertOne(chat).insertedId?.asObjectId().toString()
        messages.updateOneById(
            messageId,
            setValue(Message::chatId , chatId)
        )
    }


    override suspend fun doesChatByUsersExist(userId1: String, userId2: String): Boolean {

        return chats.find(
            and(
                Chat::userIds contains userId1,
                Chat::userIds contains userId2
            )
        ).first() != null
    }

    override suspend fun updateLastMessageIdForChat(chatId: String, lastMessageId: String) {
        chats.updateOneById(
            id = chatId,
            setValue(Chat::lastMessageId , lastMessageId)
        )
    }


}