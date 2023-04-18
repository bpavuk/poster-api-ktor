package com.bpavuk.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val username: String,
    val profileImg: String,
    val id: Int
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 45)
    val profileImg = varchar("profile_img", 100)
    val password = varchar("password", 50)

    override val primaryKey = PrimaryKey(id)
}