package com.leaderboard.plugins

import com.leaderboard.DatabaseManager
import com.leaderboard.ILeaderBoard
import com.leaderboard.SecureStore
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

            if (name.isNullOrBlank()) {
                call.respondText("Name is required", status = HttpStatusCode.BadRequest)
                return@get
            }
            val score = databaseManager.getElementByName(name)
            call.respond(score)
        }
        post("/score/{name}") {
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
                // Create new user
                val salt = SecureStore.createSalt()
                val hash = SecureStore.hashPassword(pwd, salt)
                databaseManager.setScore(name, newScore, hash, salt)

                println("New user created with name: $name")
            }
            else
            {
                val hashData = databaseManager.getPasswordAndSalt(name)
                if(!SecureStore.secureCheck(pwd, hashData.salt, hashData.password)) {
                    call.respondText("Incorrect password", status = HttpStatusCode.Unauthorized)
                    return@post
                }

                databaseManager.setScore(name, newScore)
            }

            call.respond(HttpStatusCode.OK, "Score updated")
        }
        get("/leaderboard") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val leaderBoard = databaseManager.getLeaderBoard(limit)
            call.respond(leaderBoard)
        }
        post("/auth/{name}") {
            val name = call.parameters["name"]
            val pwd = call.receiveText()
            call.application.environment.log.info("auth: name=$name")

            if (name.isNullOrBlank() || pwd.isBlank()) {
                call.respondText("Name and password are required", status = HttpStatusCode.BadRequest)
                return@post
            }

            if(!databaseManager.userExists(name)) {
                val salt = SecureStore.createSalt()
                val hash = SecureStore.hashPassword(pwd, salt)
                databaseManager.setScore(name, 0, hash, salt)

                println("New user created with name: $name")
                call.respondText("New user was created", status = HttpStatusCode.OK)
                return@post
            }

            val hashData = databaseManager.getPasswordAndSalt(name)
            if(!SecureStore.secureCheck(pwd, hashData.salt, hashData.password)) {
                call.respondText("Incorrect password", status = HttpStatusCode.Unauthorized)
                return@post
            }

            println("User authenticated with name: $name")
            call.respond(HttpStatusCode.OK, "Authenticated")
        }
        post("/auth/change/{name}") {
            val name = call.parameters["name"]
            val body = call.receiveText().split(";")
            call.application.environment.log.info("changePassword: name=$name -> $body")
            if(body.size != 2) {
                call.respondText("New and old passwords are required", status = HttpStatusCode.BadRequest)
                return@post
            }

            val oldPwd = body[0]
            val newPwd = body[1]
            if (name.isNullOrBlank() || newPwd.isBlank() || oldPwd.isBlank()) {
                call.respondText("Name, new and old passwords can't be empty", status = HttpStatusCode.BadRequest)
                return@post
            }

            if(!databaseManager.userExists(name)) {
                call.respondText("User does not exist", status = HttpStatusCode.OK)
                return@post
            }

            val hashData = databaseManager.getPasswordAndSalt(name)
            if(!SecureStore.secureCheck(oldPwd, hashData.salt, hashData.password)) {
                call.respondText("Incorrect password", status = HttpStatusCode.Unauthorized)
                return@post
            }

            val salt = SecureStore.createSalt()
            val hash = SecureStore.hashPassword(newPwd, salt)
            databaseManager.changePassword(name, hash, salt)

            call.respond(HttpStatusCode.OK, "Password changed")
        }
    }
}
