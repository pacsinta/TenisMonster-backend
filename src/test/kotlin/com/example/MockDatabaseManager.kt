package com.example

import com.leaderboard.*
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

object MockDatabaseManager : ILeaderBoard {
    override suspend fun getElementByName(name: String): LeaderBoardElement {
        return LeaderBoardElement(name, 0)
    }

    override suspend fun setScore(name: String, score: Int, password: ByteArray, salt: ByteArray) {

    }

    override suspend fun getPasswordAndSalt(name: String): PasswordAndSalt {
        return PasswordAndSalt(ByteArray(0), ByteArray(0))
    }

    override suspend fun changePassword(name: String, newPassword: ByteArray, salt: ByteArray) {

    }

    override suspend fun getLeaderBoard(limit: Int): List<LeaderBoardElement> {
        return emptyList()
    }

    override suspend fun userExists(name: String): Boolean {
        return false
    }
}

object InMemoryDatabase : DatabaseManagerBase() {
    fun deleteAll() {
        transaction {
            LeaderBoard.deleteAll()
        }
    }
}