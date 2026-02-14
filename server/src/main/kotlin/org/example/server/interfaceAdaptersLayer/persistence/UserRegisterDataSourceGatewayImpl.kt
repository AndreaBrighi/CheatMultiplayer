package org.example.server.interfaceAdaptersLayer.persistence

import org.example.server.businessLayer.adapter.login.LoginDataSourceResponseModel
import org.example.server.businessLayer.adapter.user.UserDataSourceRequestModel
import org.example.server.businessLayer.boundaries.UserRegisterDataSourceGateway

class UserRegisterDataSourceGatewayImpl(
    userRepository: UserRepository,
) : UserRegisterDataSourceGateway {

    override fun existsByName(name: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun save(requestModel: UserDataSourceRequestModel) {
        TODO("Not yet implemented")
    }

    override fun findUser(name: String): LoginDataSourceResponseModel? {
        TODO("Not yet implemented")
    }
}