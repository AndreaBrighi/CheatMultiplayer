package org.example.server.businessLayer

import org.example.server.businessLayer.adapter.login.LoginRequestModel
import org.example.server.businessLayer.adapter.login.LoginResponseModel
import org.example.server.businessLayer.adapter.TokenResponseModel
import org.example.server.businessLayer.adapter.user.UserDataSourceRequestModel
import org.example.server.businessLayer.adapter.user.UserRequestModel
import org.example.server.businessLayer.adapter.user.UserResponseModel
import org.example.server.businessLayer.boundaries.UserInputBoundary
import org.example.server.businessLayer.boundaries.UserRegisterDataSourceGateway
import org.example.server.businessLayer.boundaries.UserSecurity
import org.example.server.businessLayer.exception.PasswordToShortException
import org.example.server.businessLayer.exception.UserAlreadyPresentException
import org.example.server.domainLayer.User
import java.time.LocalDateTime

class UserRegisterUseCase(
    private val userDataSourceGateway: UserRegisterDataSourceGateway,
    private val userSecurity: UserSecurity,
) : UserInputBoundary {
    override fun createUser(requestModel: UserRequestModel): Result<UserResponseModel> {
        val now = LocalDateTime.now()
        if (userDataSourceGateway.existsByName(requestModel.name)) {
            return Result.failure(UserAlreadyPresentException())
        }

        if (requestModel.password.length < 8) {
            return Result.failure(PasswordToShortException())
        }

        val hashedPassword = userSecurity.getHash(requestModel.password)
        val user: User = User.create(requestModel.name, hashedPassword)
        val userDataSourceModel =
            UserDataSourceRequestModel(
                user.name,
                hashedPassword,
                now,
            )
        userDataSourceGateway.save(userDataSourceModel)
        val token = userSecurity.generateToken(user.name)

        val accountResponseModel = UserResponseModel(user.name, token, now.toString())
        return Result.success(accountResponseModel)
    }

    override fun login(requestModel: LoginRequestModel): Result<LoginResponseModel> {
        TODO("Not yet implemented")
    }

    override fun checkUserToken(token: String): Result<TokenResponseModel> {
        TODO("Not yet implemented")
    }
}
