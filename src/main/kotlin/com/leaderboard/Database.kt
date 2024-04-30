package com.leaderboard

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseManager : DatabaseManagerBase()

open class DatabaseManagerBase : ILeaderBoard {
    fun init(jdbcUrl: String = "jdbc:h2:file:./build/db") {
        val driverClassName = "org.h2.Driver"
        val database = Database.connect(jdbcUrl, driverClassName)

        transaction(database) {
            SchemaUtils.create(LeaderBoard)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    private fun resultRowToPlayerInfo(row: ResultRow) = LeaderBoardElement(
        name = row[LeaderBoard.name],
        score = row[LeaderBoard.score]
    )

    override suspend fun getElementByName(name: String): LeaderBoardElement = dbQuery {
        LeaderBoard.select { LeaderBoard.name eq name }
            .mapNotNull { resultRowToPlayerInfo(it) }
            .singleOrNull() ?: LeaderBoardElement(name, 0)
    }

    override suspend fun setScore(name: String, score: Int): Unit = dbQuery {
        if (LeaderBoard.select { LeaderBoard.name eq name }.count() == 0L) {
            LeaderBoard.insert {
                it[LeaderBoard.name] = name
                it[LeaderBoard.score] = score
            }
        } else {
            LeaderBoard.update({ LeaderBoard.name eq name }) {
                it[LeaderBoard.score] = score
            }
        }
    }

    override suspend fun getLeaderBoard(limit: Int): List<LeaderBoardElement> = dbQuery {
        LeaderBoard.selectAll().limit(limit).orderBy(LeaderBoard.score to SortOrder.DESC).map { resultRowToPlayerInfo(it) }
    }
}