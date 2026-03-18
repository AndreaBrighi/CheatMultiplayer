package org.example.server.adapters.controllers.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.example.server.application.ports.models.user.UserRequestModel

class CreateUserRequestDto
    @JsonCreator
    constructor(
        @param:JsonProperty("username")
        val username: String,
        @param:JsonProperty("password")
        val password: String,
    )

fun CreateUserRequestDto.toModel(): UserRequestModel = UserRequestModel(username, password)
