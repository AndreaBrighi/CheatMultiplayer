package org.example.server.businessLayer.boundaries

import org.example.server.businessLayer.adapter.TokenResponseModel
import org.example.server.businessLayer.adapter.login.LoginRequestModel
import org.example.server.businessLayer.adapter.login.LoginResponseModel
import org.example.server.businessLayer.adapter.user.UserRequestModel
import org.example.server.businessLayer.adapter.user.UserResponseModel


interface UserInputBoundary {
    fun createUser(requestModel: UserRequestModel): Result<UserResponseModel>

    fun login(requestModel: LoginRequestModel): Result<LoginResponseModel>

    fun checkUserToken(token: String): Result<TokenResponseModel>

    /*
    fun changePassword(requestModel: UserRequestModel): Result<UserResponseModel>

    fun deleteUser(requestModel: UserRequestModel): Result<UserResponseModel>

    fun changeRole(requestModel: UserRequestModel): Result<UserResponseModel>

     */
}
