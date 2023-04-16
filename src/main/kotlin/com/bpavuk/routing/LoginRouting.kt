package com.bpavuk.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.bpavuk.models.User
import com.bpavuk.models.userStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Routing.loginRouting() {
    val secret = environment!!.config.property("jwt.secret").getString()
    val issuer = environment!!.config.property("jwt.issuer").getString()
    val audience = environment!!.config.property("jwt.audience").getString()

    post("/login") {
        val user = call.receive<User>()
        val token = if (user in userStorage.values) {
            JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", user.username)
                .withClaim("password", generateUtterTrash())
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(secret))
        } else null
        token?.let {
            call.respond(mapOf("token" to token))
        } ?: call.respondText(
            "Invalid username or password",
            status = HttpStatusCode.Unauthorized
        )
    }
}

// This function sent me to go fuck myself in CAPS!
fun generateUtterTrash(): String {
    val length = (1..40).random()
    var trash = ""
    val symbolSet = '0'..'z'
    for (i in 0 until length) {
        trash += symbolSet.random()
    }
    return trash
}