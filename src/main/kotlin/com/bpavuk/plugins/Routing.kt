package com.bpavuk.plugins

import com.bpavuk.routing.helloRouting
import com.bpavuk.routing.loginRouting
import com.bpavuk.routing.postsRouting
import com.bpavuk.routing.userRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        loginRouting()
        helloRouting()
        userRouting()
        postsRouting()
    }
}
