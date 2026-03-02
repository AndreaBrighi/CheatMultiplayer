package org.example.server.application.usecases

import org.example.server.application.ports.PasswordSecurity
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserInputBoundary
import org.example.server.application.ports.UserRegisterDataSourceGateway
import org.example.server.application.ports.models.UserInfoResponse
import org.example.server.application.ports.models.exception.PasswordToShortException
import org.example.server.application.ports.models.exception.UserAlreadyPresentException
import org.example.server.application.ports.models.exception.UserNotFound
import org.example.server.application.ports.models.login.LoginRequestModel
import org.example.server.application.ports.models.login.LoginResponseModel
import org.example.server.application.ports.models.user.UserDataSourceRequestModel
import org.example.server.application.ports.models.user.UserRequestModel
import org.example.server.application.ports.models.user.UserResponseModel
import org.example.server.domain.model.User
import java.time.LocalDateTime

class UserRegisterUseCase(
    private val userDataSourceGateway: UserRegisterDataSourceGateway,
    private val passwordSecurity: PasswordSecurity,
    private val tokenSecurity: TokenSecurity,
) : UserInputBoundary {
    override fun createUser(requestModel: UserRequestModel): Result<UserResponseModel> {
        val now = LocalDateTime.now()
        if (userDataSourceGateway.existsByUsername(requestModel.username)) {
            return Result.failure(UserAlreadyPresentException())
        }
        if (requestModel.password.length < 8) {
            return Result.failure(PasswordToShortException())
        }

        val hashedPassword = passwordSecurity.hash(requestModel.password)
        val user: User = User.create(requestModel.username, hashedPassword)
        val userDataSourceModel =
            UserDataSourceRequestModel(
                user.username,
                hashedPassword,
                now,
            )
        val saveResult = userDataSourceGateway.save(userDataSourceModel)
        if (saveResult.isFailure) {
            return Result.failure(saveResult.exceptionOrNull() ?: Exception("Unknown error"))
        }
        val token = tokenSecurity.generateToken(user.username)
        val accountResponseModel = UserResponseModel(user.username, token, now.toString())
        return Result.success(accountResponseModel)
    }

    override fun login(requestModel: LoginRequestModel): Result<LoginResponseModel> {
        val findUserResult = userDataSourceGateway.findUser(requestModel.username)
        if (findUserResult.isFailure) {
            return Result.failure(UserNotFound())
        }
        val user = findUserResult.getOrNull() ?: return Result.failure(UserNotFound())
        if (!passwordSecurity.matches(requestModel.password, user.password)) {
            return Result.failure(Exception("Invalid password"))
        }
        val token = tokenSecurity.generateToken(requestModel.username)
        val loginResponseModel = LoginResponseModel(requestModel.username, token)
        return Result.success(loginResponseModel)
    }

    override fun getUser(username: String): Result<UserInfoResponse> {
        val findUserResult = userDataSourceGateway.findUser(username)
        if (findUserResult.isFailure) {
            return Result.failure(UserNotFound())
        }
        val user = findUserResult.getOrNull() ?: return Result.failure(UserNotFound())
        val tokenResponseModel = UserInfoResponse(user.username, user.createdAt)
        return Result.success(tokenResponseModel)
    }
}
