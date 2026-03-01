package org.example.server.application.ports

interface PasswordSecurity {
    fun hash(password: String): String

    fun matches(
        password: String,
        hash: String,
    ): Boolean
}
