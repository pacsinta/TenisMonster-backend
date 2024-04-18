package com.example

import com.leaderboard.ILeaderBoard
import com.leaderboard.LeaderBoardElement

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