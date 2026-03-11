package org.example.server.adapters.controllers.presenter

import org.example.server.adapters.controllers.dto.ErrorResponseDto
import org.example.server.adapters.controllers.dto.toDto
import org.example.server.application.ports.CreateUserOutputBoundary
import org.example.server.application.ports.models.user.UserResponseModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class CreateUserHttpPresenter : CreateUserOutputBoundary {
    private var responseEntity: ResponseEntity<Any>? = null

    override fun presentSuccess(response: UserResponseModel) {
        val body = response.toDto()
        responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(body)
    }

    override fun presentUserAlreadyExists(message: String) {
        val body = ErrorResponseDto(message)
        responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    override fun presentPasswordError(message: String) {
        val body = ErrorResponseDto(message)
        responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    override fun presentSaveError(message: String) {
        val body = ErrorResponseDto(message)
        responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    fun toResponseEntity(): ResponseEntity<Any> = responseEntity ?: ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR)
}
