package com.bpavuk.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class UserForm(val username: String, val password: String): Principal

val userFormStorage = mutableMapOf(
    "hello" to UserForm("hello", "itsMe"),
    "bpavuk" to UserForm("bpavuk", "bpavuk")
)