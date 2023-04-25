package com.bpavuk.routing

import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.postsRouting() {
    get("/posts/{post_id}") {

    }
    get("/posts") {

    }
    get("/uploads/{user_id}/{file_name}") {

    }
    authenticate {
        post("/posts/new") {

        }
        delete("/posts/{post_id}") {

        }
    }
}