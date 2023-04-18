package com.bpavuk.dao

import com.bpavuk.dao.DatabaseFactory.dbQuery
import com.bpavuk.models.User
import com.bpavuk.models.UserRegisterForm
import com.bpavuk.models.Users
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        profileImg = row[Users.profileImg],
    )

    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun addUser(user: UserRegisterForm): User? = dbQuery {
        val insertStatement = Users.insert {
            it[username] = user.username
            it[password] = user.password
            it[profileImg] = user.imageUrl
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun getUser(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
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