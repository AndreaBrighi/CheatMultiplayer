package org.example.server.application.ports.models.login

data class LoginRequestModel(
    val username: String,
    val password: String,
)
