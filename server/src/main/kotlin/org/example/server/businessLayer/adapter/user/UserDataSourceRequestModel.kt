package org.example.server.businessLayer.adapter.user

import java.time.LocalDateTime

data class UserDataSourceRequestModel(
    val name: String,
    val password: String,
    val now: LocalDateTime,
)
