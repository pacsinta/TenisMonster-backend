package com.leaderboard

import com.leaderboard.database.DatabaseManager
import com.leaderboard.plugins.configureRouting
import com.leaderboard.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 6000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseManager.init()
    configureSerialization()
    configureRouting()
}
