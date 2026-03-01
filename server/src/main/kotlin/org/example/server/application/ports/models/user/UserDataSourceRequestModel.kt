package org.example.server.application.ports.models.user

import java.time.LocalDateTime

data class UserDataSourceRequestModel(
    val name: String,
    val password: String,
    val now: LocalDateTime,
)
