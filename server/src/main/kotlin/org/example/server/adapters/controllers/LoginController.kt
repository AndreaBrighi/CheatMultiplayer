package org.example.server.adapters.controllers

import org.example.server.application.ports.UserInputBoundary
import org.example.server.adapters.controllers.dto.LoginRequestDto
import org.example.server.adapters.controllers.dto.toDto
import org.example.server.adapters.controllers.dto.toModel
import org.example.server.application.ports.models.authentication.AuthenticatedUser
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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

        @GetMapping("private/user")
        fun getUser(
            @AuthenticationPrincipal principal: AuthenticatedUser
        ): HttpEntity<Any> {
            val response = userInputBoundary.getUser(principal.username)
            return if (response.isSuccess) {
                ResponseEntity(response.getOrNull() ?: false, HttpStatus.OK)
            } else {
                ResponseEntity(
                    response.exceptionOrNull()?.message
                        ?: "Invalid token",
                    HttpStatus.UNAUTHORIZED,
                )
            }
        }
}
