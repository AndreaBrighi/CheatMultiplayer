package org.example.server.infrastructure.security

import org.example.server.application.ports.PasswordSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordSecurityImpl : PasswordSecurity {
    private val encoder = BCryptPasswordEncoder()

    override fun hash(password: String): String = encoder.encode(password)

    override fun matches(
        password: String,
        hash: String,
    ): Boolean = encoder.matches(password, hash)
}
