package org.example.server.businessLayer

import org.example.server.businessLayer.adapter.authentication.AuthenticatedUser

interface UserAuthenticationGateway {
    fun loadUserByUsername(username: String): AuthenticatedUser?
}
