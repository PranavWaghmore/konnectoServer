package pw.coding.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import pw.coding.plugins.userId


val ApplicationCall.userId: String
    get() = principal<JWTPrincipal>()?.userId.toString()