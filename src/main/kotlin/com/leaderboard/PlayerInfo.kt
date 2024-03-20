package com.leaderboard

import org.jetbrains.exposed.sql.Table

data class PlayerInfo(val name: String, val score: Int)

object LeaderBoard : Table() {
    val name = varchar("name", 50)
    val score = integer("score")

    override val primaryKey = PrimaryKey(name)
}