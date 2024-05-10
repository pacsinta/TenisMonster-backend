package com.leaderboard.routes

import com.leaderboard.database.DatabaseManager
import com.leaderboard.database.ILeaderBoard
import com.leaderboard.securestore.ISecureStore
import com.leaderboard.securestore.SecureStore
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(databaseManager: ILeaderBoard = DatabaseManager, secureStore: ISecureStore = SecureStore) {
    configureAuthRouting(databaseManager, secureStore)
    configureScoreRouting(databaseManager, secureStore)

    routing {
        get("/leaderboard") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val leaderBoard = databaseManager.getLeaderBoard(limit)
            call.respond(leaderBoard)
        }
    }
}
