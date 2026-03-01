package org.example.server.domain.model

interface User {
    val name: String

    val password: String

    companion object {
        fun create(
            name: String,
            password: String,
        ): User =
            object : User {
                override val name: String = name
                override val password: String = password
            }
    }
}