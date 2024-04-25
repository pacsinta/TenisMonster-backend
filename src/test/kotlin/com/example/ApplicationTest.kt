package com.example

import com.leaderboard.plugins.configureRouting
import com.leaderboard.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testGetScore() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager)
        }
        val response = client.get("/score/playerName")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{\"name\":\"playerName\",\"score\":0}", response.bodyAsText())
    }

    @Test
    fun testSetEmptyScore() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager)
        }
        val response = client.post("/score/playerName")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testSetScore() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager)
        }
        val response = client.post("/score/playerName") {
            body = "100"
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testSetNegativeScore() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager)
        }
        val response = client.post("/score/playerName") {
            body = "-100"
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetLeaderBoard() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager)
        }
        val response = client.get("/leaderboard")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }
}
