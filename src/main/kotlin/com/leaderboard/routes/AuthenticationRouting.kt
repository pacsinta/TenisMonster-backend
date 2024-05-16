package com.leaderboard.routes

import com.leaderboard.database.ILeaderBoard
import com.leaderboard.securestore.ISecureStore
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureAuthRouting(databaseManager: ILeaderBoard, secureStore: ISecureStore) {
    route("/auth") {
        post("/{name}") {
            val name = call.parameters["name"]
            val pwd = call.receiveText()
            call.application.environment.log.info("auth: name=$name")

            if (name.isNullOrBlank() || pwd.isBlank()) {
                call.respondText("Name and password are required", status = HttpStatusCode.BadRequest)
                return@post
            } else if (name.length > 50) {
                call.respondText("Name is too long", status = HttpStatusCode.BadRequest)
                return@post
            }

            if (!databaseManager.userExists(name)) {
                val salt = secureStore.createSalt()
                val hash = secureStore.hashPassword(pwd, salt)
                databaseManager.setScore(name, 0, hash, salt)

                println("New user created with name: $name")
                call.respondText("New user was created", status = HttpStatusCode.OK)
                return@post
            }

            val hashData = databaseManager.getPasswordAndSalt(name)
            if (!secureStore.secureCheck(pwd, hashData.salt, hashData.password)) {
                call.respondText("Incorrect password", status = HttpStatusCode.Unauthorized)
                return@post
            }

            println("User authenticated with name: $name")
            call.respond(HttpStatusCode.OK, "Authenticated")
        }
        post("/change/{name}") {
            val name = call.parameters["name"]
            val body = call.receiveText().split(";")
            call.application.environment.log.info("changePassword: name=$name -> $body")
            if (body.size != 2) {
                call.respondText("New and old passwords are required", status = HttpStatusCode.BadRequest)
                return@post
            }

            val oldPwd = body[0]
            val newPwd = body[1]
            if (name.isNullOrBlank() || newPwd.isBlank() || oldPwd.isBlank()) {
                call.respondText("Name, new and old passwords can't be empty", status = HttpStatusCode.BadRequest)
                return@post
            }

            if (!databaseManager.userExists(name)) {
                call.respondText("User does not exist", status = HttpStatusCode.NotFound)
                return@post
            }

            val hashData = databaseManager.getPasswordAndSalt(name)
            if (!secureStore.secureCheck(oldPwd, hashData.salt, hashData.password)) {
                call.respondText("Incorrect password", status = HttpStatusCode.Unauthorized)
                return@post
            }

            val salt = secureStore.createSalt()
            val hash = secureStore.hashPassword(newPwd, salt)
            databaseManager.changePassword(name, hash, salt)

            call.respond(HttpStatusCode.OK, "Password changed")
        }
    }
}