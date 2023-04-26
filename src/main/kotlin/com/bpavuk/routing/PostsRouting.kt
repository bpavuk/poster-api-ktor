package com.bpavuk.routing

import com.bpavuk.dao.dao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.io.File

fun Route.postsRouting() {
    get("/posts/{post_id}") {
        val postId = call.parameters.getOrFail("post_id").toInt()

        call.respond(dao.getPost(id = postId))
    }
    get("/posts") {

    }
    get("/uploads/{user_id}/{file_name}") {
        val userId = call.parameters.getOrFail("user_id").toInt()
        val fileName = call.parameters.getOrFail("file_name")

        val file = File("uploads/$userId/$fileName")
        if (!file.exists()) {
            call.respond(HttpStatusCode.NotFound, "File not found :p")
        } else {
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, fileName)
                    .toString()
            )
            call.respondFile(file)
        }
    }
    authenticate {
        post("/posts/new") {

        }
        delete("/posts/{post_id}") {

        }
    }
}