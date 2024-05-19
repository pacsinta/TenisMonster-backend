package com.example

import com.leaderboard.plugins.configureSerialization
import com.leaderboard.routes.configureRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTest {
    private fun Application.configureModules() {
        configureSerialization()
        configureRouting(MockDatabaseManager, MockSecureStore)
    }
    
    @Test
    fun testGetScore() = testApplication {
        application {
            configureModules()
        }
        val response = client.get("/score/playerName")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{\"name\":\"playerName\",\"score\":0}", response.bodyAsText())
    }

    @Test
    fun testSetEmptyScore() = testApplication {
        application {
            configureModules()
        }
        val response = client.post("/score/playerName")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testSetScore() = testApplication {
        application {
            configureModules()
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
            configureModules()
        }
        val response = client.post("/score/playerName") {
            body = "-100;pwd"
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetLeaderBoard() = testApplication {
        application {
            configureModules()
        }
        val response = client.get("/leaderboard")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @OptIn(InternalAPI::class)
    @Test
    fun testAuth() = testApplication {
        application {
            configureModules()
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
            configureModules()
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
            configureModules()
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
            configureModules()
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
            configureModules()
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
            configureModules()
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
            configureModules()
        }
        val response = client.post("/auth/change/playerName") {
            body = ""
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
