package com.example

import com.leaderboard.plugins.configureRouting
import com.leaderboard.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class DatabaseTest {
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
    fun testScoreUpload() = runBlocking  {
        testApplication {
            application {
                configureSerialization()
                configureRouting(InMemoryDatabase)
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
                configureRouting(InMemoryDatabase)
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

    @OptIn(InternalAPI::class)
    @Test
    fun testLeaderboard() = runBlocking {
        testApplication {
            application {
                configureSerialization()
                configureRouting(InMemoryDatabase)
            }

            for (i in 1..5) {
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
}