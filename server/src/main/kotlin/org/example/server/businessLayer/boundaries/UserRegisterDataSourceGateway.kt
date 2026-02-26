package org.example.server.businessLayer.boundaries

import org.example.server.businessLayer.adapter.login.LoginDataSourceResponseModel
import org.example.server.businessLayer.adapter.user.UserDataSourceRequestModel

interface UserRegisterDataSourceGateway {
    fun existsByName(name: String): Boolean

    fun save(requestModel: UserDataSourceRequestModel): Result<Long>

    fun findUser(name: String): Result<LoginDataSourceResponseModel>
}
