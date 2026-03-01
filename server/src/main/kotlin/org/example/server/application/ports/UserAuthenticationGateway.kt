package org.example.server.application.ports

import org.example.server.application.ports.models.authentication.AuthenticatedUser

interface UserAuthenticationGateway {
    fun loadUserByUsername(username: String): AuthenticatedUser?
}
