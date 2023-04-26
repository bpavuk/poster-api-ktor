package com.bpavuk.routing

import com.bpavuk.dao.dao
import com.bpavuk.models.UserRegisterForm
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.io.File
import java.util.*

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
    get("/users/search") {
        val query = call.request.queryParameters.getOrFail("q")
        call.respond(dao.searchUser(query))
    }
    authenticate {
        get("/users/me") {
            val principal = call.principal<JWTPrincipal>()
            val id = principal!!.payload.getClaim("id").asInt()

            dao.getUser(id)?.let { call.respond(status = HttpStatusCode.Accepted, it) }
                ?: call.respond(status = HttpStatusCode.Unauthorized, "Wrong token!")
        }
        delete("/users/me}") {
            val principal = call.principal<JWTPrincipal>()
            val id = principal!!.payload.getClaim("id").asInt()

            if (dao.deleteUser(id)) {
                call.respond(status = HttpStatusCode.Accepted, message = "Farewell, dear user")
            } else {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Sorry, your deletion was unsuccessful due to problems at our side. Contact us and we'll try to resolve this problem"
                )
            }
        }
        put("/users/changeAvatar") {
            val principal = call.principal<JWTPrincipal>()
            val id = principal!!.payload.getClaim("id").asInt()
            val multiPartData = call.receiveMultipart()
            var fileName = ""
            multiPartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val match = ".*\\.(jpg|jpeg|png|gif)".toRegex().matchEntire(part.originalFileName!!) as MatchResult
                        if (match.value == part.originalFileName) {
                            fileName = UUID.randomUUID().toString()
                            val fileBytes = part.streamProvider().readBytes()
                            val file = File("./uploads/$id/$fileName.${match.groupValues[1]}")
                            if (!file.parentFile.exists()) file.parentFile.mkdirs()
                            file.createNewFile()
                            file.writeBytes(fileBytes)
                        } else {
                            throw IllegalArgumentException("Wrong file extensions! Only .jpg, .jpeg, .png and .gif are allowed")
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            dao.editUserAvatar(id, "uploads/$id/$fileName")
            call.respond("Successful change of avatar, ${principal.payload.getClaim("username")}")
        }
    }
}