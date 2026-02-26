package org.example.server.businessLayer

import org.example.server.businessLayer.adapter.TokenResponseModel
import org.example.server.businessLayer.adapter.login.LoginRequestModel
import org.example.server.businessLayer.adapter.login.LoginResponseModel
import org.example.server.businessLayer.adapter.user.UserDataSourceRequestModel
import org.example.server.businessLayer.adapter.user.UserRequestModel
import org.example.server.businessLayer.adapter.user.UserResponseModel
import org.example.server.businessLayer.boundaries.UserInputBoundary
import org.example.server.businessLayer.boundaries.UserRegisterDataSourceGateway
import org.example.server.businessLayer.boundaries.UserSecurity
import org.example.server.businessLayer.exception.PasswordToShortException
import org.example.server.businessLayer.exception.UserAlreadyPresentException
import org.example.server.businessLayer.exception.UserNotFound
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
        val saveResult = userDataSourceGateway.save(userDataSourceModel)
        if (saveResult.isFailure) {
            return Result.failure(saveResult.exceptionOrNull() ?: Exception("Unknown error"))
        }
        val token = userSecurity.generateToken(user.name)
        val accountResponseModel = UserResponseModel(user.name, token, now.toString())
        return Result.success(accountResponseModel)
    }

    override fun login(requestModel: LoginRequestModel): Result<LoginResponseModel> {
        val findUserResult = userDataSourceGateway.findUser(requestModel.username)
        if (findUserResult.isFailure) {
            return Result.failure(UserNotFound())
        }
        val user = findUserResult.getOrNull() ?: return Result.failure(UserNotFound())
        if (!userSecurity.checkPassword(requestModel.password, user.password)) {
            return Result.failure(Exception("Invalid password"))
        }
        val token = userSecurity.generateToken(requestModel.username)
        val loginResponseModel = LoginResponseModel(requestModel.username, token)
        return Result.success(loginResponseModel)
    }

    override fun checkUserToken(token: String): Result<TokenResponseModel> {
        TODO("Not yet implemented")
    }
}
