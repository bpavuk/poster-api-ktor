package com.bpavuk.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.partialcontent.*

fun Application.configurePartialContent() {
    install(PartialContent) {
        // Maximum number of ranges that will be accepted from a HTTP request.
        // If the HTTP request specifies more ranges, they will all be merged into a single range.
        maxRangeCount = 10
    }
}