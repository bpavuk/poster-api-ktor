package com.bpavuk

import com.bpavuk.di.DatabaseFactory
import com.bpavuk.di.appModule
import com.bpavuk.plugins.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    DatabaseFactory.init()
    configureHTTP()
    configureSecurity()
    configureSerialization()
    configurePartialContent()
    configureAutoHeadResponse()
    configureRouting()
}
