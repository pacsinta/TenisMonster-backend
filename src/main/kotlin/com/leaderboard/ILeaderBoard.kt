package com.leaderboard

interface ILeaderBoard {
    suspend fun getElementByName(name: String): LeaderBoardElement
    suspend fun setScore(name: String, score: Int)
    suspend fun getLeaderBoard(limit: Int): List<LeaderBoardElement>
}