package com.example

import com.leaderboard.*
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

object MockDatabaseManager : ILeaderBoard {
    override suspend fun getElementByName(name: String): LeaderBoardElement {
        return LeaderBoardElement(name, 0)
    }

    override suspend fun setScore(name: String, score: Int) {

    }

    override suspend fun getLeaderBoard(limit: Int): List<LeaderBoardElement> {
        return emptyList()
    }
}

object InMemoryDatabase : DatabaseManagerBase() {
    fun deleteAll() {
        transaction {
            LeaderBoard.deleteAll()
        }
    }
}