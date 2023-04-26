package com.bpavuk.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


object Posts : Table() {
    val id = integer("id").autoIncrement()
    val description = varchar(name = "description", length = 1000)
    val photosList = text("photos_list")
    val authorId = reference("author", Users.id)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Post(val id: Int, val description: String, val photosList: List<String>, val authorId: Int)
