package org.example.server.interfaceAdaptersLayer.controllers.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.example.server.businessLayer.adapter.login.LoginResponseModel

class LoginResponseDto
    @JsonCreator
    constructor(
        @param:JsonProperty("name")
        val name: String,
        @param:JsonProperty("token")
        val token: String,
    )

fun LoginResponseModel.toDto(): LoginResponseDto = LoginResponseDto(name, token)
