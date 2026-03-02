package org.example.server.infrastructure.persistence

import org.example.server.application.ports.UserRegisterDataSourceGateway
import org.example.server.application.ports.models.login.LoginDataSourceResponseModel
import org.example.server.application.ports.models.user.UserDataSourceRequestModel
import org.example.server.infrastructure.persistence.entity.UserEntity

class UserRegisterDataSourceGatewayImpl(
    val userRepository: UserRepository,
) : UserRegisterDataSourceGateway {
    override fun existsByUsername(username: String): Boolean = userRepository.findByUsername(username) != null

    override fun save(requestModel: UserDataSourceRequestModel): Result<Long> {
        val userEntity =
            UserEntity(
                username = requestModel.username,
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

    override fun findUser(username: String): Result<LoginDataSourceResponseModel> =
        userRepository.findByUsername(username)?.let { user ->
            Result.success(
                LoginDataSourceResponseModel(
                    username = user.username,
                    password = user.password,
                    createdAt = user.createdAt!!,
                ),
            )
        } ?: Result.failure(Exception("User not found"))
}
