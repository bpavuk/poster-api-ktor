package com.bpavuk.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val password: String): Principal

val userStorage = mutableMapOf(
    "hello" to User("hello", "itsMe"),
    "bpavuk" to User("bpavuk", "bpavuk")
)