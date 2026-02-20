package org.example.server.interfaceAdaptersLayer.controllers

import org.example.server.businessLayer.boundaries.UserInputBoundary
import org.example.server.interfaceAdaptersLayer.controllers.dto.LoginRequestDto
import org.example.server.interfaceAdaptersLayer.controllers.dto.toDto
import org.example.server.interfaceAdaptersLayer.controllers.dto.toModel
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class LoginController(
    val userInputBoundary: UserInputBoundary,
) {

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDto,
    ): HttpEntity<Any> {
        val response = userInputBoundary.login(loginRequest.toModel())
        return if (response.isSuccess) {
            ResponseEntity(response.map { r -> r.toDto() }, HttpStatus.OK)
        } else {
            ResponseEntity(
                response.exceptionOrNull()?.message
                    ?: "Invalid credentials",
                HttpStatus.UNAUTHORIZED,
            )
        }
    }
}
