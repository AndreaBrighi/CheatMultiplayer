package org.example.server.application.ports.models.user

data class UserResponseModel(
    val username: String,
    val token: String,
    val time: String,
)
