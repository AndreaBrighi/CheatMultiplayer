package org.example.server

import org.example.server.businessLayer.UserAuthenticationGateway
import org.example.server.businessLayer.UserRegisterUseCase
import org.example.server.businessLayer.boundaries.UserInputBoundary
import org.example.server.businessLayer.boundaries.UserRegisterDataSourceGateway
import org.example.server.businessLayer.boundaries.UserSecurity
import org.example.server.interfaceAdaptersLayer.persistence.UserAuthenticationGatewayImpl
import org.example.server.interfaceAdaptersLayer.persistence.UserRegisterDataSourceGatewayImpl
import org.example.server.interfaceAdaptersLayer.persistence.UserRepository
import org.example.server.interfaceAdaptersLayer.security.UserSecurityImpl
import org.example.server.interfaceAdaptersLayer.security.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class AppConfig {
    @Bean
    fun userInput(
        userSecurity: UserSecurity,
        userRegisterDataSourceGateway: UserRegisterDataSourceGateway,
    ): UserInputBoundary {
        val userRegisterUseCase = UserRegisterUseCase(userRegisterDataSourceGateway, userSecurity)
        return userRegisterUseCase
    }

    @Bean
    fun userSecurity(environment: Environment): UserSecurity {
        val key = environment.getProperty("security.jwt.secret-key")
        assert(key != null)
        val security = UserSecurityImpl(key!!)
        return security
    }

    @Bean
    fun userRegisterDataSourceGateway(userRepository: UserRepository): UserRegisterDataSourceGateway {
        val userRegisterDataSourceGateway = UserRegisterDataSourceGatewayImpl(userRepository)
        return userRegisterDataSourceGateway
    }

    @Bean
    fun userAuthenticationGateway(userRepository: UserRepository): UserAuthenticationGateway {
        val userAuthenticationGateway = UserAuthenticationGatewayImpl(userRepository)
        return userAuthenticationGateway
    }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        userSecurity: UserSecurity,
        userAuthenticationGateway: UserAuthenticationGateway,
    ): SecurityFilterChain {
        http {
            csrf { disable() }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            formLogin { disable() }
            httpBasic { disable() }

            authorizeHttpRequests {
                authorize("/api/login", permitAll)
                authorize("/api/public/**", permitAll)
                authorize("/api/private/**", authenticated)
                authorize(anyRequest, denyAll)
            }

            val jwtFilter = JwtAuthenticationFilter(userSecurity, userAuthenticationGateway)
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtFilter)
        }

        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService = UserDetailsService { throw UsernameNotFoundException("Not used") }
}
