package org.example.server.businessLayer.exception

class PasswordToShortException : Exception() {
    override val message: String
        get() = "User password must have more than 5 characters."
}
