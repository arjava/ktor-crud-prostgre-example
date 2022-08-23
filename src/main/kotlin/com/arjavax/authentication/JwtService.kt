package com.arjavax.authentication

import com.arjavax.data.model.User
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class JwtService {

    private val issuer = "noteServer"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun generateToken(user: User): String {
        return JWT.create().withSubject("NoteAuthentication").withIssuer(issuer).withClaim("email", user.email)
            .sign(algorithm)
    }

}