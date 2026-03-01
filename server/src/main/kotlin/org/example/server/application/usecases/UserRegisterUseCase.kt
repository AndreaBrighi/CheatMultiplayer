package org.example.server.application.usecases

import org.example.server.application.ports.PasswordSecurity
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserInputBoundary
import org.example.server.application.ports.UserRegisterDataSourceGateway
import org.example.server.application.ports.models.TokenResponseModel
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
        if (userDataSourceGateway.existsByName(requestModel.name)) {
            return Result.failure(UserAlreadyPresentException())
        }
        if (requestModel.password.length < 8) {
            return Result.failure(PasswordToShortException())
        }

        val hashedPassword = passwordSecurity.hash(requestModel.password)
        val user: User = User.Companion.create(requestModel.name, hashedPassword)
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
        val token = tokenSecurity.generateToken(user.name)
        val accountResponseModel = UserResponseModel(user.name, token, now.toString())
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

    override fun checkUserToken(token: String): Result<TokenResponseModel> {
        TODO("Not yet implemented")
    }
}
