package pw.coding.data.repository.activity

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import pw.coding.data.models.Activity
import pw.coding.data.models.User
import pw.coding.data.responses.ActivityResponse

class ActivityRepositoryImpl(
    db:CoroutineDatabase
):ActivityRepository {

    private val activities = db.getCollection<Activity>()
    private val users = db.getCollection<User>()
    override suspend fun getActivitiesForUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<ActivityResponse> {
        val activityList = activities.find(Activity::toUserId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Activity::timestamp)
            .toList()

        val userIds = activityList.map { it.byUserId }.distinct()
        val userMap = users.find(User::id `in` userIds)
            .toList()
            .associateBy { it.id }

        return activityList.map { activity ->
            ActivityResponse(
                userId = activity.byUserId,
                parentId = activity.parentId,
                username = userMap[activity.byUserId]?.username ?: "",
                type = activity.type,
                timeStamp = activity.timestamp,
                id = activity.id
            )
        }
    }


    override suspend fun createActivity(activity: Activity) {
         activities.insertOne(activity)
    }

    override suspend fun deleteActivity(activityId: String): Boolean {
        return activities.deleteOneById(activityId).wasAcknowledged()
    }
}