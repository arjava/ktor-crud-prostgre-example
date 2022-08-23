package com.arjavax.routes

import com.arjavax.authentication.JwtService
import com.arjavax.data.model.User
import com.arjavax.data.model.request.LoginRequest
import com.arjavax.data.model.request.RegisterRequest
import com.arjavax.data.model.response.BaseResponse
import com.arjavax.repository.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"

fun Route.UserRoutes(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String)->String
) {
    post(REGISTER_REQUEST){
        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e:Exception){
            call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Missing Some Fields"))
            return@post
        }

        try {
            val user = User(registerRequest.email, hashFunction(registerRequest.password), registerRequest.username)
            db.addUser(user)
            call.respond(HttpStatusCode.OK, BaseResponse(true, jwtService.generateToken(user)))
        }catch (e:Exception){
            call.respond(HttpStatusCode.Conflict, BaseResponse(false, e.message ?: "Some Problem Occurred"))
        }
    }

    post(LOGIN_REQUEST){
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Missing some fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)

            if(user == null){
                call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Wrong email ID"))
            }else {

                if (user.hashPassword == hashFunction(loginRequest.password)){
                    call.respond(HttpStatusCode.OK, BaseResponse(true, jwtService.generateToken(user)))
                }else {
                    call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Password incorrect!"))
                }
            }
        }catch (e:Exception){
            call.respond(HttpStatusCode.Conflict, BaseResponse(false, e.message ?: "Some Problem Occurred"))
        }
    }
}