package org.example.server.interfaceAdaptersLayer.persistence

import org.example.server.businessLayer.adapter.login.LoginDataSourceResponseModel
import org.example.server.businessLayer.adapter.user.UserDataSourceRequestModel
import org.example.server.businessLayer.boundaries.UserRegisterDataSourceGateway
import org.example.server.interfaceAdaptersLayer.persistence.dao.UserEntity

class UserRegisterDataSourceGatewayImpl(
    val userRepository: UserRepository,
) : UserRegisterDataSourceGateway {
    override fun existsByName(name: String): Boolean = userRepository.findByName(name) != null

    override fun save(requestModel: UserDataSourceRequestModel): Result<Long> {
        val userEntity =
            UserEntity(
                name = requestModel.name,
                password = requestModel.password,
                createdAt = requestModel.now,
            )
        return try {
            val savedUser = userRepository.save(userEntity)
            Result.success(savedUser.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun findUser(name: String): Result<LoginDataSourceResponseModel> =
        userRepository.findByName(name)?.let { user ->
            Result.success(
                LoginDataSourceResponseModel(
                    name = user.name,
                    password = user.password,
                ),
            )
        } ?: Result.failure(Exception("User not found"))
}
