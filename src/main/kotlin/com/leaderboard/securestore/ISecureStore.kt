package com.leaderboard.securestore

interface ISecureStore {
    fun createSalt(): ByteArray
    fun hashPassword(password: String, salt: ByteArray): ByteArray
    fun secureCheck(password: String, salt: ByteArray, hash: ByteArray): Boolean
}