package org.example.server.application.ports

import org.example.server.application.ports.models.UserInfoResponse

interface GetUserOutputBoundary {
    fun presentSuccess(response: UserInfoResponse)

    fun presentUserNotFound(message: String)
}
