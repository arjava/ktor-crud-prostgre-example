package com.arjavax.data.model.request

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)
