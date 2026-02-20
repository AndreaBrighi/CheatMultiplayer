package org.example.server.interfaceAdaptersLayer.persistence

import org.example.server.businessLayer.UserAuthenticationGateway
import org.example.server.businessLayer.adapter.authentication.AuthenticatedUser

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