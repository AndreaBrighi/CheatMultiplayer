package org.example.server.interfaceAdaptersLayer.controllers

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration
class TestSecurityConfig {
    @Bean
    fun testSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() } // or keep enabled and use .with(csrf()) in tests
            authorizeHttpRequests {
                authorize("/api/greeting/**", permitAll)
                authorize("/api/register/**", permitAll)
                authorize("/api/login/**", permitAll) // make login public if desired
                authorize("/**", permitAll)
            }
        }
        return http.build()
    }
}