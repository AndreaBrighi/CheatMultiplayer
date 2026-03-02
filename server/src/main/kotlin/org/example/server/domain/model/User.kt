package org.example.server.domain.model

interface User {
    val username: String

    val password: String

    companion object {
        fun create(
            username: String,
            password: String,
        ): User =
            object : User {
                override val username: String = username
                override val password: String = password
            }
    }
}