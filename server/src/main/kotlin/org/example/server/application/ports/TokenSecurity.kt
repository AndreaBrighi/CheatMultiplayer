package org.example.server.application.ports

interface TokenSecurity {
    fun generateToken(username: String): String

    fun isTokenValid(token: String): Result<Boolean>

    fun extractUsername(token: String): String
}
