package org.example.server.application.ports

import org.example.server.application.ports.models.login.LoginDataSourceResponseModel
import org.example.server.application.ports.models.user.UserDataSourceRequestModel

interface UserRegisterDataSourceGateway {
    fun existsByName(name: String): Boolean

    fun save(requestModel: UserDataSourceRequestModel): Result<Long>

    fun findUser(name: String): Result<LoginDataSourceResponseModel>
}
