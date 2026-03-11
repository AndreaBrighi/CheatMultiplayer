package org.example.server.adapters.controllers.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.example.server.application.ports.models.user.UserResponseModel

class CreateUserResponseDto
    @JsonCreator
    constructor(
        @param:JsonProperty("username")
        val username: String,
        @param:JsonProperty("token")
        val token: String,
    )

fun UserResponseModel.toDto(): CreateUserResponseDto = CreateUserResponseDto(username, token)
