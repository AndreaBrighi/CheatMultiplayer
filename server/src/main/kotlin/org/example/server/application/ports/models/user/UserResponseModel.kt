package org.example.server.application.ports.models.user

data class UserResponseModel(
    val name: String,
    val token: String,
    val time: String,
)
