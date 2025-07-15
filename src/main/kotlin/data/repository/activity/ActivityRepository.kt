package pw.coding.data.repository.activity

import pw.coding.data.models.Activity
import pw.coding.util.Constants

interface ActivityRepository {

    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.ACTIVITY_PAGE_SIZE
    ):List<Activity>

    suspend fun createActivity(activity: Activity)

    suspend fun deleteActivity(activityId: String):Boolean
}