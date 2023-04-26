package com.bpavuk.dao

import com.bpavuk.dao.DatabaseFactory.dbQuery
import com.bpavuk.models.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        profileImg = row[Users.profileImg],
    )

    private fun resultRowToPost(row: ResultRow) = Post(
        id = row[Posts.id],
        photosList = row[Posts.photosList].split("\t"),
        description = row[Posts.description],
        authorId = row[Posts.authorId]
    )

    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun addUser(user: UserRegisterForm): User? = dbQuery {
        val insertStatement = if (searchUser(user.username).find {
                it.username == user.username
            } == null) Users.insert {
            it[username] = user.username
            it[password] = user.password
            it[profileImg] = user.imageUrl
        } else null

        insertStatement?.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun getUser(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun loginUser(
        username: String,
        password: String
    ): Boolean = dbQuery {
        Users.select { (Users.username eq username) and (Users.password eq password) }
            .map(::resultRowToUser)
            .singleOrNull() != null
    }

    override suspend fun getPost(id: Int): Post = dbQuery {
        Posts.select { Posts.id eq id }
            .map(::resultRowToPost)
            .singleOrNull() ?: throw IllegalArgumentException(s = "No posts with ID $id found")
    }

    override suspend fun searchUser(username: String): List<User> = dbQuery {
        Users.select { Users.username like "%$username%" }
            .map(::resultRowToUser)
    }

    override suspend fun editUserAvatar(id: Int, imgUrl: String): Boolean = dbQuery {
        Users.update({ Users.id eq id }) {
            it[profileImg] = imgUrl
        } > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
        if (allUsers().isEmpty()) {
            addUser(
                UserRegisterForm(
                    username = "bpavuk",
                    imageUrl = "https://picsum.photos/200",
                    password = "bpavuk"
                )
            )
        }
    }
}