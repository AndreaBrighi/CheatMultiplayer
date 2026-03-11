package org.example.server.adapters.controllers.presenter

import org.example.server.adapters.controllers.dto.ErrorResponseDto
import org.example.server.adapters.controllers.dto.toDto
import org.example.server.application.ports.LoginOutputBoundary
import org.example.server.application.ports.models.login.LoginResponseModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class LoginHttpPresenter : LoginOutputBoundary {
    private var responseEntity: ResponseEntity<Any>? = null

    override fun presentSuccess(response: LoginResponseModel) {
        val body = response.toDto()
        responseEntity = ResponseEntity.ok(body)
    }

    override fun presentInvalidCredentials(message: String) {
        val body = ErrorResponseDto(message)
        responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body)
    }

    override fun presentUserNotFound(message: String) {
        val body = ErrorResponseDto(message)
        responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body)
    }

    fun toResponseEntity(): ResponseEntity<Any> {
        return responseEntity ?: ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
