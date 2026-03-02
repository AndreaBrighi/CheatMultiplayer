package org.example.server.application.ports.models.login

data class LoginResponseModel(
    val username: String,
    val token: String,
)
