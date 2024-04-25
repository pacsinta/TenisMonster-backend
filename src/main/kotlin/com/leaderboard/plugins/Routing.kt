package com.leaderboard.plugins

import com.leaderboard.DatabaseManager
import com.leaderboard.ILeaderBoard
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(databaseManager: ILeaderBoard = DatabaseManager) {
    routing {
        get("/score/{name}") {
            val name = call.parameters["name"]
            call.application.environment.log.info("getScore: name=$name")

            if (name == null) {
                call.respondText("Name is required", status = HttpStatusCode.BadRequest)
                return@get
            }
            val score = databaseManager.getElementByName(name)
            call.respond(score)
        }
        post("/score/{name}") {
            val name = call.parameters["name"]
            val body = call.receiveText()
            call.application.environment.log.info("setScore: name=$name -> $body")

            val score = body.toIntOrNull()
            if (name == null || score == null) {
                call.respondText("Name and score are required", status = HttpStatusCode.BadRequest)
                return@post
            }
            val newScore = (databaseManager.getElementByName(name).score + score).coerceAtLeast(0)
            databaseManager.setScore(name, newScore)

            call.respond(HttpStatusCode.OK, "Score updated")
        }
        get("/leaderboard") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val leaderBoard = databaseManager.getLeaderBoard(limit)
            call.respond(leaderBoard)
        }
    }
}
