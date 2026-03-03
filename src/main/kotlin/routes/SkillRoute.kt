package pw.coding.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.response.respond
import pw.coding.service.SkillsService

fun Route.getSkills(
    skillsService: SkillsService
){
    authenticate{
        get("/api/skills/get"){
            call.respond(
                HttpStatusCode.OK,
                skillsService.getSkills().map { it.toSkillDto() }
            )
        }
    }
}