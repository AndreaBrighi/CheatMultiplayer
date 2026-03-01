package org.example.server.application.ports.models.exception

class PasswordToShortException : Exception() {
    override val message: String
        get() = "User password must have more than 5 characters."
}
