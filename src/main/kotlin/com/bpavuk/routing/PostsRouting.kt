package com.bpavuk.routing

import com.bpavuk.dao.dao
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

fun Route.postsRouting() {
    get("/posts/{post_id}") {
        val postId = call.parameters.getOrFail("post_id").toInt()

        call.respond(dao.getPost(id = postId))
    }
    get("/posts") {
        val startId = call.request.queryParameters.getOrFail("start").toInt()
        val amount = call.request.queryParameters.getOrFail("amount").toInt()

        call.respond(dao.getPosts(start = startId, amount = amount))
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
            val principal = call.principal<JWTPrincipal>()
            val id = principal!!.payload.getClaim("id").asInt()
            val postMultipart = call.receiveMultipart()
            val photosList = mutableListOf<String>()
            var description = ""

            postMultipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val match = "(.*)\\.(jpg|jpeg|png|gif)".toRegex().matchEntire(part.originalFileName!!) as MatchResult
                        if (match.value == part.originalFileName) {
                            val fileName = "${UUID.randomUUID()}.${match.groupValues[1]}"
                            val fileBytes = part.streamProvider().readBytes()
                            val file = File("./uploads/$id/$fileName")
                            if (!file.parentFile.exists()) file.parentFile.mkdirs()
                            file.createNewFile()
                            file.writeBytes(fileBytes)
                            photosList.add(fileName)
                        } else {
                            throw IllegalArgumentException(
                                s = "Wrong file extensions! Only .jpg, .jpeg, .png and .gif are allowed"
                            )
                        }
                    }
                    is PartData.FormItem -> {
                        description = part.value
                    }
                    else -> {
                        throw IllegalArgumentException(s = "Unexpected element")
                    }
                }
                part.dispose()
            }

            call.respond(dao.newPost(photos = photosList, postDescription = description, userId = id))
        }
        delete("/posts/{post_id}") {

        }
    }
}