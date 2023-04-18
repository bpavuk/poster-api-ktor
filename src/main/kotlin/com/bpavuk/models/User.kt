package com.bpavuk.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
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
    ),
    User(
        username = "hello",
        id = 1,
        profileImg = "https://picsum.photos/200"
    )
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 45)
    val profileImg = varchar("profile_img", 100)
    val password = varchar("password", 50)

    override val primaryKey = PrimaryKey(id)
}