package org.example.server.application.ports.models.user

data class UserRequestModel(
    val username: String,
    val password: String,
)
