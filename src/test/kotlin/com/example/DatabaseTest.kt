package com.example

import com.leaderboard.plugins.configureSerialization
import com.leaderboard.routes.configureRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class DatabaseTest {
    @Before
    fun setupDatabase() = runBlocking {
        // The DB_CLOSE_DELAY=-1 parameter keeps the in-memory database open until the tests are running
        TestDatabaseManager.init("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

        TestDatabaseManager.addUser("playerName")
        for (i in 1..numberOfPlayers) {
            TestDatabaseManager.addUser("player$i")
        }
    }

    @After
    fun cleanDatabase() {
        TestDatabaseManager.deleteAll()
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testScoreUpload() = runBlocking  {
        testApplication {
            application {
                configureSerialization()
                configureRouting(TestDatabaseManager, MockSecureStore)
            }

            val post = client.post("/score/playerName") {
                body = "2;pwd"
            }
            assertEquals(HttpStatusCode.OK, post.status)
            delay(1000) // wait for the database to update

            val response = client.get("/score/playerName")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"name\":\"playerName\",\"score\":2}", response.bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testNegativeScore() = runBlocking {
        testApplication {
            application {
                configureSerialization()
                configureRouting(TestDatabaseManager, MockSecureStore)
            }

            val post = client.post("/score/playerName") {
                body = "-2;pwd"
            }
            assertEquals(HttpStatusCode.OK, post.status)
            delay(1000) // wait for the database to update

            val response = client.get("/score/playerName")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"name\":\"playerName\",\"score\":0}", response.bodyAsText())
        }
    }

    private val numberOfPlayers = 5
    @OptIn(InternalAPI::class)
    @Test
    fun testLeaderboard() = runBlocking {
        testApplication {
            application {
                configureSerialization()
                configureRouting(TestDatabaseManager, MockSecureStore)
            }

            for (i in 1..numberOfPlayers) {
                val post = client.post("/score/player$i") {
                    body = "$i;pwd"
                }
                assertEquals(HttpStatusCode.OK, post.status)
            }
            delay(1000) // wait for the database to update

            val response = client.get("/leaderboard") {
                parameter("limit", "3")
            }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("[{\"name\":\"player5\",\"score\":5},{\"name\":\"player4\",\"score\":4},{\"name\":\"player3\",\"score\":3}]", response.bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testUserRegistration() = runBlocking {
        testApplication {
            application {
                configureSerialization()
                configureRouting(TestDatabaseManager, MockSecureStore)
            }

            TestDatabaseManager.deleteAll() // clean every user

            val post = client.post("/auth/newPlayer") {
                body = "pwd"
            }
            assertEquals(HttpStatusCode.OK, post.status)
            delay(1000) // wait for the database to update

            val response = client.get("/leaderboard")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("[{\"name\":\"newPlayer\",\"score\":0}]", response.bodyAsText())
        }
    }
}