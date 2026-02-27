package org.example.server.interfaceAdaptersLayer.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.server.businessLayer.adapter.login.LoginRequestModel
import org.example.server.businessLayer.adapter.login.LoginResponseModel
import org.example.server.businessLayer.boundaries.UserInputBoundary
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(LoginController::class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest(
    @Autowired
    val mockMvc: MockMvc,
    @Autowired
    val objectMapper: ObjectMapper,
) {
    @MockitoBean
    lateinit var userInputBoundary: UserInputBoundary

    @Test
    fun `login success returns response dto`() {
        val responseModel = LoginResponseModel(name = "Alice", token = "token123")
        whenever(userInputBoundary.login(any<LoginRequestModel>()))
            .thenReturn(Result.success(responseModel))

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
    }

    @Test
    fun `login failure returns unauthorized and message`() {
        whenever(userInputBoundary.login(any<LoginRequestModel>()))
            .thenReturn(Result.failure(Exception("bad creds")))

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
    }
}
