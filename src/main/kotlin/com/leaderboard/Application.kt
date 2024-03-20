package com.example

import com.example.plugins.*
import com.leaderboard.DatabaseManager
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
