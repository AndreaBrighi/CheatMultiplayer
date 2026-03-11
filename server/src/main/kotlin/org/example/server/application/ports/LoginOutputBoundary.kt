package org.example.server.application.ports

import org.example.server.application.ports.models.login.LoginResponseModel

interface LoginOutputBoundary {
    fun presentSuccess(response: LoginResponseModel)

    fun presentInvalidCredentials(message: String)

    fun presentUserNotFound(message: String)
}
