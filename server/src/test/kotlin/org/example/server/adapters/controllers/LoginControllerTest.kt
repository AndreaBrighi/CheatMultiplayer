package org.example.server.adapters.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.server.application.ports.CreateUserOutputBoundary
import org.example.server.application.ports.LoginOutputBoundary
import org.example.server.application.ports.UserInputBoundary
import org.example.server.application.ports.models.login.LoginRequestModel
import org.example.server.application.ports.models.login.LoginResponseModel
import org.example.server.application.ports.models.user.UserResponseModel
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@WebMvcTest(LoginController::class)
class LoginControllerTest :
    FunSpec({
        val userInputBoundary: UserInputBoundary = mockk()
        val controller = LoginController(userInputBoundary)
        val objectMapper = ObjectMapper()
        val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()

        test("create user success return response dto") {
            val responseModel =
                UserResponseModel(username = "Alice", token = "token123", time = LocalDateTime.now().toString())
            every { userInputBoundary.createUser(any(), any()) } answers {
                val presenter = secondArg<CreateUserOutputBoundary>()
                presenter.presentSuccess(responseModel)
            }

            val request = mapOf("username" to "Alice", "password" to "pass")
            val json = objectMapper.writeValueAsString(request)

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json),
                ).andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect { MockMvcResultMatchers.jsonPath("$.token").value("token123") }
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Alice"))

            verify {
                userInputBoundary.createUser(
                    match { it.username == "Alice" && it.password == "pass" },
                    any(),
                )
            }
            confirmVerified(userInputBoundary)
        }

        test("login success returns response dto") {
            val responseModel = LoginResponseModel(username = "Alice", token = "token123")
            every { userInputBoundary.login(any<LoginRequestModel>(), any()) } answers {
                val presenter = secondArg<LoginOutputBoundary>()
                presenter.presentSuccess(responseModel)
            }

            val request = mapOf("username" to "Alice", "password" to "pass")
            val json = objectMapper.writeValueAsString(request)

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Alice"))

            verify {
                userInputBoundary.login(
                    match { it.username == "Alice" && it.password == "pass" },
                    any(),
                )
            }
            confirmVerified(userInputBoundary)
        }

        test("login failure returns unauthorized and message") {
            every { userInputBoundary.login(any<LoginRequestModel>(), any()) } answers {
                val presenter = secondArg<LoginOutputBoundary>()
                presenter.presentInvalidCredentials("bad creds")
            }

            val request = mapOf("username" to "Alice", "password" to "wrong")
            val json = objectMapper.writeValueAsString(request)

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json),
                ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("bad creds"))

            verify {
                userInputBoundary.login(
                    match { it.username == "Alice" && it.password == "wrong" },
                    any(),
                )
            }
            confirmVerified(userInputBoundary)
        }
    })
