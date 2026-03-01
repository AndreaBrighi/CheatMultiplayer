package org.example.server.infrastructure.configuration

import org.example.server.application.ports.UserAuthenticationGateway
import org.example.server.application.ports.UserRegisterDataSourceGateway
import org.example.server.infrastructure.persistence.UserAuthenticationGatewayImpl
import org.example.server.infrastructure.persistence.UserRegisterDataSourceGatewayImpl
import org.example.server.infrastructure.persistence.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistenceConfig {
    @Bean
    fun userRegisterDataSourceGateway(userRepository: UserRepository): UserRegisterDataSourceGateway =
        UserRegisterDataSourceGatewayImpl(userRepository)

    @Bean
    fun userAuthenticationGateway(userRepository: UserRepository): UserAuthenticationGateway = UserAuthenticationGatewayImpl(userRepository)
}
