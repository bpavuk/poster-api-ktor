package com.bpavuk.models

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterForm(
    val username: String,
    val password: String,
    val imageUrl: String
)
