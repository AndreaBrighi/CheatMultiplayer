package org.example.server.application.ports.models.user

import java.time.LocalDateTime

data class UserDataSourceRequestModel(
    val username: String,
    val password: String,
    val now: LocalDateTime,
)
