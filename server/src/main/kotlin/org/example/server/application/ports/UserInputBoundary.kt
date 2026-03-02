package org.example.server.application.ports

import org.example.server.application.ports.models.UserInfoResponse
import org.example.server.application.ports.models.login.LoginRequestModel
import org.example.server.application.ports.models.login.LoginResponseModel
import org.example.server.application.ports.models.user.UserRequestModel
import org.example.server.application.ports.models.user.UserResponseModel


interface UserInputBoundary {
    fun createUser(requestModel: UserRequestModel): Result<UserResponseModel>

    fun login(requestModel: LoginRequestModel): Result<LoginResponseModel>

    fun getUser(username: String): Result<UserInfoResponse>

    /*
    fun changePassword(requestModel: UserRequestModel): Result<UserResponseModel>

    fun deleteUser(requestModel: UserRequestModel): Result<UserResponseModel>

    fun changeRole(requestModel: UserRequestModel): Result<UserResponseModel>

     */
}
