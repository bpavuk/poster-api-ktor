package com.bpavuk.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val password: String): Principal
