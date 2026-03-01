package org.example.server.interfaceAdaptersLayer.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.server.businessLayer.adapter.login.LoginRequestModel
import org.example.server.businessLayer.adapter.login.LoginResponseModel
import org.example.server.businessLayer.boundaries.UserInputBoundary
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@WebMvcTest(LoginController::class)
class LoginControllerTest :
    FunSpec({
        val userInputBoundary: UserInputBoundary = mockk()
        val controller = LoginController(userInputBoundary)
        val objectMapper = ObjectMapper()
        val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()

        test("login success returns response dto") {
            val responseModel = LoginResponseModel(name = "Alice", token = "token123")
            every { userInputBoundary.login(any<LoginRequestModel>()) } returns Result.success(responseModel)

            val request = mapOf("username" to "user1", "password" to "pass")
            val json = objectMapper.writeValueAsString(request)

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token123"))

            verify { userInputBoundary.login(any()) }
        }

        test("login failure returns unauthorized and message") {
            every { userInputBoundary.login(any<LoginRequestModel>()) } returns Result.failure(Exception("bad creds"))

            val request = mapOf("username" to "user1", "password" to "wrong")
            val json = objectMapper.writeValueAsString(request)

            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json),
                ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.content().string("bad creds"))

            verify { userInputBoundary.login(any()) }
        }
    })
