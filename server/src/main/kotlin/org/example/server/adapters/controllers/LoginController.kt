package org.example.server.adapters.controllers

import org.example.server.adapters.controllers.dto.LoginRequestDto
import org.example.server.adapters.controllers.dto.toModel
import org.example.server.adapters.controllers.presenter.GetUserHttpPresenter
import org.example.server.adapters.controllers.presenter.LoginHttpPresenter
import org.example.server.application.ports.UserInputBoundary
import org.example.server.application.ports.models.authentication.AuthenticatedUser
import org.springframework.http.HttpEntity
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
        val presenter = LoginHttpPresenter()
        userInputBoundary.login(loginRequest.toModel(), presenter)
        return presenter.toResponseEntity()
    }

    @GetMapping("private/user")
    fun getUser(
        @AuthenticationPrincipal principal: AuthenticatedUser,
    ): HttpEntity<Any> {
        val presenter = GetUserHttpPresenter()
        userInputBoundary.getUser(principal.username, presenter)
        return presenter.toResponseEntity()
    }
}
