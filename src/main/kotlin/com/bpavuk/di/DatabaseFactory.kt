package com.bpavuk.di

import com.bpavuk.models.Posts
import com.bpavuk.models.Users
import kotlinx.coroutines.Dispatchers
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcUrl = "jdbc:h2:file:./build/db"
        val tables = listOf(Users, Posts)

        Database.connect(jdbcUrl, driverClassName)

        val flyway = Flyway.configure()
            .baselineOnMigrate(true)
            .dataSource(jdbcUrl, "", "")
            .load()

        if (flyway.info().current() != null) {
            flyway.migrate()
        }

        transaction {
            tables.forEach { table ->
                SchemaUtils.createMissingTablesAndColumns(table)
            }
        }

        if (flyway.info().current() == null) {
            flyway.migrate()
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}