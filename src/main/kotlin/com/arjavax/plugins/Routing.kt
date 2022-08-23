package com.arjavax.plugins

import com.arjavax.authentication.JwtService
import com.arjavax.data.model.User
import com.arjavax.repository.Repo
import com.arjavax.routes.UserRoutes
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting(db: Repo, jwtService: JwtService, hashFunction: (String) -> String) {
    routing {
        get("/"){
            call.respondText("Bismillah")
        }
        get("/token"){
            val email = call.request.queryParameters["email"]!!
            val username = call.request.queryParameters["username"]!!
            val password = call.request.queryParameters["password"]!!

            val user = User(email = email, userName = username, hashPassword = hashFunction(password))
            call.respond(jwtService.generateToken(user))
        }
        UserRoutes(db, jwtService, hashFunction)
    }
}
