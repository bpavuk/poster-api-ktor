package com.bpavuk.models

data class User(
    val username: String,
    val profileImg: String,
    val id: Int
)

val userStorage = mutableListOf(
    User(
        username = "bpavuk",
        id = 0,
        profileImg = "https://picsum.photos/200"
    )
)