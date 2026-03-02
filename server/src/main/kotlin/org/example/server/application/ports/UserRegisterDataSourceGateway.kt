package org.example.server.application.ports

import org.example.server.application.ports.models.login.LoginDataSourceResponseModel
import org.example.server.application.ports.models.user.UserDataSourceRequestModel

interface UserRegisterDataSourceGateway {
    fun existsByUsername(username: String): Boolean

    fun save(requestModel: UserDataSourceRequestModel): Result<Long>

    fun findUser(username: String): Result<LoginDataSourceResponseModel>
}
