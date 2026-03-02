package org.example.server.application.ports.models.login

import java.time.LocalDateTime

data class LoginDataSourceResponseModel(
    val username: String,
    val password: String,
    val createdAt: LocalDateTime,
)
