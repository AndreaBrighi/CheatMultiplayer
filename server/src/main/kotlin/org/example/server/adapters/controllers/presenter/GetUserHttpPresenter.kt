package org.example.server.adapters.controllers.presenter

import org.example.server.adapters.controllers.dto.ErrorResponseDto
import org.example.server.adapters.controllers.dto.toDto
import org.example.server.application.ports.GetUserOutputBoundary
import org.example.server.application.ports.models.UserInfoResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class GetUserHttpPresenter : GetUserOutputBoundary {
    private var responseEntity: ResponseEntity<Any>? = null

    override fun presentSuccess(response: UserInfoResponse) {
        val body = response.toDto()
        responseEntity = ResponseEntity.ok(body)
    }

    override fun presentUserNotFound(message: String) {
        val body = ErrorResponseDto(message)
        responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    fun toResponseEntity(): ResponseEntity<Any> = responseEntity ?: ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR)
}
