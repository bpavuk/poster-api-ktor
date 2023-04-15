package com.bpavuk

import com.bpavuk.plugins.configureHTTP
import com.bpavuk.plugins.configureRouting
import com.bpavuk.plugins.configureSecurity
import com.bpavuk.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
