package org.example.server.application.ports

import org.example.server.application.ports.models.login.LoginRequestModel
import org.example.server.application.ports.models.user.UserRequestModel

interface UserInputBoundary {
    fun createUser(
        requestModel: UserRequestModel,
        presenter: CreateUserOutputBoundary,
    )

    fun login(
        requestModel: LoginRequestModel,
        presenter: LoginOutputBoundary,
    )

    fun getUser(
        username: String,
        presenter: GetUserOutputBoundary,
    )

    /*
    fun changePassword(requestModel: UserRequestModel): Result<UserResponseModel>

    fun deleteUser(requestModel: UserRequestModel): Result<UserResponseModel>

    fun changeRole(requestModel: UserRequestModel): Result<UserResponseModel>

     */
}
