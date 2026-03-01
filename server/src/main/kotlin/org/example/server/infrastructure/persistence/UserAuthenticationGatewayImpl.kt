package org.example.server.infrastructure.persistence

import org.example.server.application.ports.UserAuthenticationGateway
import org.example.server.application.ports.models.authentication.AuthenticatedUser

class UserAuthenticationGatewayImpl(
    private val userRepository: UserRepository
): UserAuthenticationGateway {
    override fun loadUserByUsername(username: String): AuthenticatedUser? {
        val user = userRepository.findByName(username)

        return if (user != null) {
            AuthenticatedUser(
                username = user.name,
                enabled = true
            )
        } else {
            null
        }
    }
}