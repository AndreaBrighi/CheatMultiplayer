package org.example.server.businessLayer.exception

class UserAlreadyPresentException : Exception() {
    override val message: String
        get() = "User already present"
}
