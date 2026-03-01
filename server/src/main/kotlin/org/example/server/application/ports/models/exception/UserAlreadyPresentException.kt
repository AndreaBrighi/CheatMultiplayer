package org.example.server.application.ports.models.exception

class UserAlreadyPresentException : Exception() {
    override val message: String
        get() = "User already present"
}
