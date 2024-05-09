package com.example

import com.leaderboard.securestore.ISecureStore

object MockSecureStore : ISecureStore {
    override fun createSalt(): ByteArray {
        return ByteArray(16)
    }

    override fun hashPassword(password: String, salt: ByteArray): ByteArray {
        return ByteArray(16)
    }

    override fun secureCheck(password: String, salt: ByteArray, hash: ByteArray): Boolean {
        return true
    }
}