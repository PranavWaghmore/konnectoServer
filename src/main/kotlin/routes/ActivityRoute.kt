package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pw.coding.service.ActivityService
import pw.coding.util.Constants
import pw.coding.util.QueryParams

fun Route.getActivitiesForUser(
    activityService: ActivityService
) {
    authenticate {
        get("/api/activity/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]
                ?.toIntOrNull() ?: Constants.ACTIVITY_PAGE_SIZE

            val activities = activityService.getActivitiesForUser(userId = call.userId , page , pageSize)
            call.respond(
                HttpStatusCode.OK,
                activities
            )
        }
    }
}