package com.bpavuk.data

import com.bpavuk.di.DatabaseFactory.dbQuery
import com.bpavuk.models.User
import com.bpavuk.models.UserRegisterForm
import com.bpavuk.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface UserRepository {
    suspend fun allUsers(): List<User>
    suspend fun addUser(user: UserRegisterForm): User?
    suspend fun getUser(id: Int): User?
    suspend fun searchUser(username: String): List<User>
    suspend fun editUserAvatar(id: Int, imgUrl: String): Boolean
    suspend fun deleteUser(id: Int): Boolean
    suspend fun loginUser(username: String, password: String): Boolean
}

class UserRepositoryImpl : UserRepository {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        profileImg = row[Users.profileImg],
    )
    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun addUser(user: UserRegisterForm): User = dbQuery {
        val insertStatement = if (searchUser(user.username).find {
                it.username == user.username
            } == null) Users.insert {
            it[username] = user.username
            it[password] = user.password
            it[profileImg] = user.imageUrl
        } else null

        insertStatement?.resultedValues?.singleOrNull()?.let(::resultRowToUser)
            ?: throw WrongCredentialsException("User with same username already exists")
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

    override suspend fun loginUser(
        username: String,
        password: String
    ): Boolean = dbQuery {
        Users.select { (Users.username eq username) and (Users.password eq password) }
            .map(::resultRowToUser)
            .singleOrNull() != null
    }

}