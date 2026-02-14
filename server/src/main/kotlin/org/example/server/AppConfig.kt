package org.example.server

import org.example.server.businessLayer.UserRegisterUseCase
import org.example.server.businessLayer.boundaries.UserInputBoundary
import org.example.server.interfaceAdaptersLayer.persistence.UserRegisterDataSourceGatewayImpl
import org.example.server.interfaceAdaptersLayer.persistence.UserRepository
import org.example.server.interfaceAdaptersLayer.security.UserSecurityImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.authorization.SingleResultAuthorizationManager.permitAll
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
class AppConfig {

    @Bean
    fun userInput(environment: Environment, userRepository: UserRepository): UserInputBoundary {
        val userRegisterDataSourceGateway = UserRegisterDataSourceGatewayImpl(userRepository)
        val key = environment.getProperty("security.jwt.secret-key")
        assert(key != null)
        val security = UserSecurityImpl(key!!)
        val userRegisterUseCase = UserRegisterUseCase(userRegisterDataSourceGateway, security)
        return userRegisterUseCase
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/**", permitAll())
            }
        }
        return http.build()
    }
}
