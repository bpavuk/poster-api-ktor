package com.bpavuk.routing

import com.bpavuk.dao.dao
import com.bpavuk.models.UserRegisterForm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
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
    post("/users/register") {
        val newUser = call.receive<UserRegisterForm>()

        if (dao.addUser(newUser) != null) call.respond(
            status = HttpStatusCode.Accepted,
            "New user is registered"
        ) else call.respond(
            status = HttpStatusCode.NotAcceptable,
            "New user is not registered. Check your credentials"
        )
    }
}