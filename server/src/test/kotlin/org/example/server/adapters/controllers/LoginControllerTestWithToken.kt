package org.example.server.adapters.controllers

import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import org.example.server.application.ports.GetUserOutputBoundary
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserAuthenticationGateway
import org.example.server.application.ports.UserInputBoundary
import org.example.server.application.ports.models.UserInfoResponse
import org.example.server.application.ports.models.authentication.AuthenticatedUser
import org.example.server.infrastructure.security.filter.JwtAuthenticationFilter
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

class LoginControllerTestWithToken :
    FunSpec({
        test("GET /api/private/user with valid bearer token returns user info") {
            val userInputBoundary: UserInputBoundary = mockk()
            val controller = LoginController(userInputBoundary)

            // Mocks for the JWT filter
            val tokenSecurity = mockk<TokenSecurity>()
            val userAuthGateway = mockk<UserAuthenticationGateway>()
            val jwtFilter = JwtAuthenticationFilter(tokenSecurity, userAuthGateway)

            val mockMvc: MockMvc =
                MockMvcBuilders
                    .standaloneSetup(controller)
                    .addFilter<StandaloneMockMvcBuilder>(jwtFilter, "/*")
                    .setCustomArgumentResolvers(AuthenticationPrincipalArgumentResolver())
                    .build()

            val token = "fake-token"
            val username = "alice"
            val now = LocalDateTime.now()

            every { tokenSecurity.isTokenValid(token) } returns Result.success(true)
            every { tokenSecurity.extractUsername(token) } returns username
            every { userAuthGateway.loadUserByUsername(username) } returns
                AuthenticatedUser(
                    username = username,
                    enabled = true,
                )

            val expected = UserInfoResponse(username = username, createdAt = now)
            every { userInputBoundary.getUser(username, any()) } answers {
                val presenter = secondArg<GetUserOutputBoundary>()
                presenter.presentSuccess(expected)
            }

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .get("/api/private/user")
                        .header("Authorization", "Bearer $token"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
        }
    })
