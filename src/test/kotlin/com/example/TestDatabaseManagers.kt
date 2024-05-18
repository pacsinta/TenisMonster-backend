package com.example

import com.leaderboard.database.*
import com.leaderboard.securestore.SecureStore
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object MockDatabaseManager : ILeaderBoard {
    override suspend fun getElementByName(name: String): LeaderBoardElement {
        return LeaderBoardElement(name, 0)
    }

    override suspend fun setScore(name: String, score: Int, password: ByteArray, salt: ByteArray) {

    }

    override suspend fun getPasswordAndSalt(name: String): PasswordAndSalt {
        return PasswordAndSalt(ByteArray(16), ByteArray(16))
    }

    override suspend fun changePassword(name: String, newPassword: ByteArray, salt: ByteArray) {

    }

    override suspend fun getLeaderBoard(limit: Int): List<LeaderBoardElement> {
        return emptyList()
    }

    override suspend fun userExists(name: String): Boolean {
        return true
    }
}

object TestDatabaseManager : DatabaseManagerBase() {
    fun deleteAll() {
        transaction {
            LeaderBoard.deleteAll()
        }
    }

    fun addUser(name: String){
        val salt = SecureStore.createSalt()
        val hash = SecureStore.hashPassword("pwd", salt)

        transaction {
            LeaderBoard.insert {
                it[LeaderBoard.name] = name
                it[score] = 0
                it[password] = hash
                it[LeaderBoard.salt] = salt
            }
        }
    }
}