package org.example.server.application.ports.models

import java.time.LocalDateTime

data class UserInfoResponse(
    val username: String,
    val createdAt: LocalDateTime,
)
