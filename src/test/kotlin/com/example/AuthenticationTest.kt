package com.example

import com.leaderboard.plugins.configureRouting
import com.leaderboard.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthenticationTest {
    @Before
    fun setupDatabase() {
        InMemoryDatabase.init("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
    }

    @After
    fun cleanDatabase() {
        InMemoryDatabase.deleteAll()
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testAuthentication() = runBlocking {
        testApplication {
            application {
                configureSerialization()
                configureRouting(InMemoryDatabase)
            }

            // Create a new user
            val post = client.post("/auth/playerName") {
                body = "password"
            }
            assertEquals(HttpStatusCode.OK, post.status)
            delay(1000) // wait for the database to update

            // Check score settings with incorrect password
            val post2 = client.post("/score/playerName") {
                body = "1;wrongpassword"
            }
            assertEquals(HttpStatusCode.Unauthorized, post2.status)

            // Check score settings with correct password
            val post3 = client.post("/score/playerName") {
                body = "1;password"
            }
            assertEquals(HttpStatusCode.OK, post3.status)

            // Check authentication with correct password
            val post4 = client.post("/auth/playerName") {
                body = "password"
            }
            assertEquals(HttpStatusCode.OK, post4.status)

            // Check authentication with incorrect password
            val post5 = client.post("/auth/playerName") {
                body = "wrongpassword"
            }
            assertEquals(HttpStatusCode.Unauthorized, post5.status)
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testPasswordChange() = runBlocking {
        testApplication {
            application {
                configureSerialization()
                configureRouting(InMemoryDatabase)
            }

            // Create a new user
            val post = client.post("/auth/playerName") {
                body = "password"
            }
            assertEquals(HttpStatusCode.OK, post.status)
            delay(1000) // wait for the database to update

            // Change password with incorrect old password
            val post2 = client.post("/auth/change/playerName") {
                body = "wrongPassword;newpassword"
            }
            assertEquals(HttpStatusCode.Unauthorized, post2.status)

            // Change password with correct old password
            val post3 = client.post("/auth/change/playerName") {
                body = "password;newpassword"
            }
            assertEquals(HttpStatusCode.OK, post3.status)

            // Check authentication with new password
            val post4 = client.post("/auth/playerName") {
                body = "newpassword"
            }
            assertEquals(HttpStatusCode.OK, post4.status)
        }
    }
}