package org.example.server.adapters.controllers.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.example.server.application.ports.models.UserInfoResponse

class GetUserResponseDto
    @JsonCreator
    constructor(
        @param:JsonProperty("username")
        val username: String,
        @param:JsonProperty("createdAt")
        val createdAt: String,
    )

fun UserInfoResponse.toDto(): GetUserResponseDto = GetUserResponseDto(username, createdAt.toString())

