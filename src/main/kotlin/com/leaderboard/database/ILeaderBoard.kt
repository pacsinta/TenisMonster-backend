package com.leaderboard.database

interface ILeaderBoard {
    suspend fun getElementByName(name: String): LeaderBoardElement
    suspend fun getLeaderBoard(limit: Int): List<LeaderBoardElement>
    suspend fun userExists(name: String): Boolean
    suspend fun setScore(name: String, score: Int, password: ByteArray = ByteArray(0), salt: ByteArray = ByteArray(0))
    suspend fun getPasswordAndSalt(name: String): PasswordAndSalt
    suspend fun changePassword(name: String, newPassword: ByteArray, salt: ByteArray)
}