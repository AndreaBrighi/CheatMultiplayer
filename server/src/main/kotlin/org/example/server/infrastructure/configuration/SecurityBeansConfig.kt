package org.example.server.infrastructure.configuration

import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserAuthenticationGateway
import org.example.server.infrastructure.security.JwtTokenImpl
import org.example.server.infrastructure.security.PasswordSecurityImpl
import org.example.server.infrastructure.security.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class SecurityBeansConfig {

    @Bean
    fun tokenSecurity(environment: Environment): TokenSecurity{
        val key =
            environment.getProperty("security.jwt.secret-key")
                ?: throw IllegalStateException("JWT secret key is missing")

        return JwtTokenImpl(key)
    }

    @Bean
    fun passwordSecurity() = PasswordSecurityImpl()


    @Bean
    fun jwtAuthenticationFilter(
        tokenSecurity: TokenSecurity,
        userAuthenticationGateway: UserAuthenticationGateway,
    ) = JwtAuthenticationFilter(tokenSecurity, userAuthenticationGateway)
}
