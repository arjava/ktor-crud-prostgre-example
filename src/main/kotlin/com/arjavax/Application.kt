package com.arjavax

import com.arjavax.authentication.JwtService
import com.arjavax.authentication.hash
import io.ktor.server.application.*
import com.arjavax.plugins.*
import com.arjavax.repository.DatabaseFactory
import com.arjavax.repository.Repo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    DatabaseFactory.init()
    val db = Repo()
    val jwtService = JwtService()
    val hashFunction = {s:String -> hash(s) }
    configureSecurity()
    configureSerialization()
    configureRouting(db, jwtService, hashFunction)
}
