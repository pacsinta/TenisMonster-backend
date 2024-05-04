package com.leaderboard

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecureStore {
    fun createSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = keyFactory.generateSecret(
            PBEKeySpec(password.toCharArray(), salt, 10000, 128)
        ).encoded

        return hash
    }

    fun secureCheck(password: String, salt: ByteArray, hash: ByteArray): Boolean
    {
        val newHash = hashPassword(password, salt)
        return newHash.contentEquals(hash)
    }
}