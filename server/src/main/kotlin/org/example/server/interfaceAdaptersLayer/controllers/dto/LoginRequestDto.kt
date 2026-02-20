package org.example.server.interfaceAdaptersLayer.controllers.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.example.server.businessLayer.adapter.login.LoginRequestModel

class LoginRequestDto
    @JsonCreator
    constructor(
        @param:JsonProperty("username")
        val username: String,
        @param:JsonProperty("password")
        val password: String,
    )

fun LoginRequestDto.toModel() = LoginRequestModel(username, password)
