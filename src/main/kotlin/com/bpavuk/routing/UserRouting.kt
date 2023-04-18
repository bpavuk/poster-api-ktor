package com.bpavuk.routing

import com.bpavuk.dao.dao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Routing.userRouting() {
    get("/users/{id}") {
        val id = call.parameters.getOrFail("id").toInt()
        dao.getUser(id)?.let { user ->
            call.respond(user)
        } ?: call.respond(
            status = HttpStatusCode.NotFound,
            message = "User not found"
        )
    }
}