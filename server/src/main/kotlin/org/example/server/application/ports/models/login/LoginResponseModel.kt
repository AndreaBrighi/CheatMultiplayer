package org.example.server.application.ports.models.login

data class LoginResponseModel(
    val name: String,
    val token: String,
)
