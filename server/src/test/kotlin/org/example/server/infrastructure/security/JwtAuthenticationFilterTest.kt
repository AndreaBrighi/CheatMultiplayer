package org.example.server.infrastructure.security

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.instanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserAuthenticationGateway
import org.example.server.application.ports.models.authentication.AuthenticatedUser
import org.example.server.infrastructure.security.filter.JwtAuthenticationFilter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilterTest :
    FunSpec({

        lateinit var tokenSecurity: TokenSecurity
        lateinit var userAuthGateway: UserAuthenticationGateway
        lateinit var filter: JwtAuthenticationFilter

        beforeTest {
            tokenSecurity = mockk()
            userAuthGateway = mockk()
            filter = JwtAuthenticationFilter(tokenSecurity, userAuthGateway)
            SecurityContextHolder.clearContext()
        }

        test("no Authorization header -> no authentication set") {

            val request = MockHttpServletRequest()
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            filter.doFilter(request, response, filterChain)

            SecurityContextHolder.getContext().authentication shouldBe null
            verify { filterChain.doFilter(request, response) }
        }

        test("valid token and enabled user -> authentication is set") {

            val token = "valid"

            val request = MockHttpServletRequest()
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            request.addHeader("Authorization", "Bearer $token")

            every { tokenSecurity.isTokenValid(token) } returns Result.success(true)
            every { tokenSecurity.extractUsername(token) } returns "alice"

            val user =
                mockk<AuthenticatedUser> {
                    every { username } returns "alice"
                    every { enabled } returns true
                }

            every { userAuthGateway.loadUserByUsername("alice") } returns user

            filter.doFilter(request, response, filterChain)

            val auth = SecurityContextHolder.getContext().authentication

            auth shouldNotBe null
            auth.principal shouldBe instanceOf<AuthenticatedUser>()
            val principal = auth.principal as AuthenticatedUser
            principal.username shouldBe "alice"
            auth?.isAuthenticated shouldBe true

            verify { filterChain.doFilter(request, response) }
        }

        test("invalid token -> no authentication set") {

            val token = "invalid"

            val request = MockHttpServletRequest()
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            request.addHeader("Authorization", "Bearer $token")

            every { tokenSecurity.isTokenValid(token) } returns Result.success(false)

            filter.doFilter(request, response, filterChain)

            SecurityContextHolder.getContext().authentication shouldBe null
            verify { filterChain.doFilter(request, response) }
        }

        test("token validation failure -> no authentication set") {

            val token = "invalid"

            val request = MockHttpServletRequest()
            val response = MockHttpServletResponse()
            val filterChain = mockk<FilterChain>(relaxed = true)

            request.addHeader("Authorization", "Bearer $token")

            every { tokenSecurity.isTokenValid(token) } returns Result.failure(RuntimeException())

            filter.doFilter(request, response, filterChain)

            SecurityContextHolder.getContext().authentication shouldBe null
            verify { filterChain.doFilter(request, response) }
        }
    })
