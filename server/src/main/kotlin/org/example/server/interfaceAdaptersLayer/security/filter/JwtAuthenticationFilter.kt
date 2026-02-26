package org.example.server.interfaceAdaptersLayer.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.server.businessLayer.UserAuthenticationGateway
import org.example.server.businessLayer.boundaries.UserSecurity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val userSecurity: UserSecurity,
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

        val validation = userSecurity.isTokenValid(token)

        if (validation.isSuccess && validation.getOrNull() == true) {
            val username = userSecurity.tokenUser(token)

            val user = userAuthGateway.loadUserByUsername(username)

            if (user != null && user.enabled) {
                val authentication =
                    UsernamePasswordAuthenticationToken(
                        user.username,
                        null,
                    )

                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}
