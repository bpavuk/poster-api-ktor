package com.bpavuk.plugins

import com.bpavuk.routing.helloRouting
import com.bpavuk.routing.loginRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        loginRouting()
        helloRouting()
    }
}
