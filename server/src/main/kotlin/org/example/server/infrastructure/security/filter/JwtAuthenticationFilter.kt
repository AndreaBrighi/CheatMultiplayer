package org.example.server.infrastructure.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserAuthenticationGateway
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val tokenSecurity: TokenSecurity,
    private val userAuthGateway: UserAuthenticationGateway,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header = request.getHeader("Authorization")

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.substring(7)

        val validation = tokenSecurity.isTokenValid(token)

        if (
            validation.isSuccess &&
            validation.getOrNull() == true &&
            SecurityContextHolder.getContext().authentication == null
        ) {
            val username = tokenSecurity.extractUsername(token)
            val user = userAuthGateway.loadUserByUsername(username)

            if (user != null && user.enabled) {
                val authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                        user,
                        null,
                        emptyList(), // if available
                    )

                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}
