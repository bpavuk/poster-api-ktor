package com.bpavuk.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class UserLoginForm(val username: String, val password: String): Principal

val userLoginFormStorage = mutableMapOf(
    "hello" to UserLoginForm("hello", "itsMe"),
    "bpavuk" to UserLoginForm("bpavuk", "bpavuk")
)