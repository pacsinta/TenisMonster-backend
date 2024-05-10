package com.leaderboard.routes

import com.leaderboard.database.ILeaderBoard
import com.leaderboard.securestore.ISecureStore
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureScoreRouting(databaseManager: ILeaderBoard, secureStore: ISecureStore){
    routing {
        route("/score/{name}")
        {
            get {
                val name = call.parameters["name"]
                call.application.environment.log.info("getScore: name=$name")

                if (name.isNullOrBlank()) {
                    call.respondText("Name is required", status = HttpStatusCode.BadRequest)
                    return@get
                }
                val score = databaseManager.getElementByName(name)
                call.respond(score)
            }
            post {
                val name = call.parameters["name"]
                val body = call.receiveText().split(";")
                if(body.size != 2) {
                    call.respondText("Score and password are required", status = HttpStatusCode.BadRequest)
                    return@post
                }
                call.application.environment.log.info("setScore: name=$name -> $body")

                val score = body[0].toIntOrNull()
                val pwd = body[1]
                if (name.isNullOrBlank() || score == null || pwd.isBlank()) {
                    call.respondText("Name, score and password are required", status = HttpStatusCode.BadRequest)
                    return@post
                }

                val newScore = (databaseManager.getElementByName(name).score + score).coerceAtLeast(0)
                if(!databaseManager.userExists(name)) {
                    call.respondText("User does not exist", status = HttpStatusCode.NotFound)
                    return@post
                }
                else
                {
                    val hashData = databaseManager.getPasswordAndSalt(name)
                    if(!secureStore.secureCheck(pwd, hashData.salt, hashData.password)) {
                        call.respondText("Incorrect password", status = HttpStatusCode.Unauthorized)
                        return@post
                    }

                    databaseManager.setScore(name, newScore)
                }

                call.respond(HttpStatusCode.OK, "Score updated")
            }
        }
    }
}