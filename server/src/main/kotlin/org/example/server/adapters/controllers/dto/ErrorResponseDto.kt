package org.example.server.adapters.controllers.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class ErrorResponseDto
    @JsonCreator
    constructor(
        @param:JsonProperty("message")
        val message: String,
    )
