package org.example.server.application.ports

import org.example.server.application.ports.models.user.UserResponseModel

interface CreateUserOutputBoundary {
    fun presentSuccess(response: UserResponseModel)

    fun presentUserAlreadyExists(message: String)

    fun presentPasswordError(message: String)

    fun presentSaveError(message: String)
}
