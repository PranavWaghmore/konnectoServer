package pw.coding.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pw.coding.plugins.userId


val ApplicationCall.userID: String
    get() = principal<JWTPrincipal>()?.userId.toString()