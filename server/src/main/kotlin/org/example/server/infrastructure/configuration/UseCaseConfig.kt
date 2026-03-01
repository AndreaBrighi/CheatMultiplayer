package org.example.server.infrastructure.configuration

import org.example.server.application.ports.PasswordSecurity
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserInputBoundary
import org.example.server.application.ports.UserRegisterDataSourceGateway
import org.example.server.application.usecases.UserRegisterUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfig {
    @Bean
    fun userInput(
        passwordSecurity: PasswordSecurity,
        tokenSecurity: TokenSecurity,
        userRegisterDataSourceGateway: UserRegisterDataSourceGateway,
    ): UserInputBoundary = UserRegisterUseCase(userRegisterDataSourceGateway, passwordSecurity, tokenSecurity)
}
