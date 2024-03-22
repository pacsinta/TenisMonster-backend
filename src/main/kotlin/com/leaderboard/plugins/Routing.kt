package com.leaderboard.plugins

import com.leaderboard.DatabaseManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/score/{name}") {
            val name = call.parameters["name"]
            call.application.environment.log.info("getScore: name=$name")

            if (name == null) {
                call.respondText("Name is required", status = HttpStatusCode.BadRequest)
                return@get
            }
            val score = DatabaseManager.getElementByName(name)
            call.respond(score)
        }
        post("/setScore") {
            val name = call.request.queryParameters["name"]
            val score = call.request.queryParameters["score"]
            if (name == null || score == null) {
                call.respondText("Name and score are required", status = HttpStatusCode.BadRequest)
                return@post
            }
            DatabaseManager.setScore(name, score.toInt())
            call.respond(HttpStatusCode.OK)
        }
        get("/leaderboard") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val leaderBoard = DatabaseManager.getLeaderBoard(limit)
            call.respond(leaderBoard)
        }
    }
}
