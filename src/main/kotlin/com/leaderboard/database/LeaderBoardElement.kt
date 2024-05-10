package com.leaderboard.database

import org.jetbrains.exposed.sql.Table

data class LeaderBoardElement(val name: String, val score: Int)
data class PasswordAndSalt(val password: ByteArray, val salt: ByteArray)

object LeaderBoard : Table() {
    val name = varchar("name", 50).uniqueIndex()
    val score = integer("score")
    val password = binary("password", 256)
    val salt = binary("salt", 128)

    override val primaryKey = PrimaryKey(name)
}