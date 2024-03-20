package com.leaderboard

interface ILeaderBoard {
    suspend fun getScore(name: String): Int
    suspend fun setScore(name: String, score: Int)
    suspend fun getLeaderBoard(limit: Int): List<PlayerInfo>
}