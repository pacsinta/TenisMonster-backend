package com.example

import com.leaderboard.routes.configureRouting
import com.leaderboard.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTest {
    @Test
    fun testGetScore() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.get("/score/playerName")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{\"name\":\"playerName\",\"score\":0}", response.bodyAsText())
    }

    @Test
    fun testSetEmptyScore() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/score/playerName")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testSetScore() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/score/playerName") {
            body = "100;pwd"
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testSetNegativeScore() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/score/playerName") {
            body = "-100;pwd"
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetLeaderBoard() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.get("/leaderboard")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testAuth() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/auth/playerName") {
            body = "password"
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testEmptyPasswordAuth() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/auth/playerName") {
            body = ""
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testTooLongNameAuth() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val name = "a".repeat(51)
        val response = client.post("/auth/$name") {
            body = "password"
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testPasswordChange() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/auth/change/playerName") {
            body = "password;newPassword"
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testEmptyNewPasswordChange() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/auth/change/playerName") {
            body = "password;"
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testEmptyOldPasswordChange() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/auth/change/playerName") {
            body = ";newPassword"
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testEmptyPasswordChange() = testApplication {
        application {
            configureSerialization()
            configureRouting(MockDatabaseManager, MockSecureStore)
        }
        val response = client.post("/auth/change/playerName") {
            body = ""
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
