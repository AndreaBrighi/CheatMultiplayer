package org.example.server.application.usecases

import org.example.server.application.ports.CreateUserOutputBoundary
import org.example.server.application.ports.GetUserOutputBoundary
import org.example.server.application.ports.LoginOutputBoundary
import org.example.server.application.ports.PasswordSecurity
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserInputBoundary
import org.example.server.application.ports.UserRegisterDataSourceGateway
import org.example.server.application.ports.models.UserInfoResponse
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
    override fun createUser(
        requestModel: UserRequestModel,
        presenter: CreateUserOutputBoundary,
    ) {
        val now = LocalDateTime.now()
        if (userDataSourceGateway.existsByUsername(requestModel.username)) {
            presenter.presentUserAlreadyExists("User already exists")
            return
        }
        if (requestModel.password.length < 8) {
            presenter.presentPasswordError("Password too short")
            return
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
            presenter.presentSaveError(saveResult.exceptionOrNull()?.message ?: "Unknown error")
            return
        }
        val token = tokenSecurity.generateToken(user.username)
        val accountResponseModel = UserResponseModel(user.username, token, now.toString())
        presenter.presentSuccess(accountResponseModel)
    }

    override fun login(
        requestModel: LoginRequestModel,
        presenter: LoginOutputBoundary,
    ) {
        val findUserResult = userDataSourceGateway.findUser(requestModel.username)
        if (findUserResult.isFailure) {
            presenter.presentUserNotFound("Credential error")
            return
        }
        val user =
            findUserResult.getOrNull() ?: run {
                presenter.presentUserNotFound("Credential error")
                return
            }
        if (!passwordSecurity.matches(requestModel.password, user.password)) {
            presenter.presentInvalidCredentials("Credential error")
            return
        }
        val token = tokenSecurity.generateToken(requestModel.username)
        presenter.presentSuccess(LoginResponseModel(requestModel.username, token))
    }

    override fun getUser(
        username: String,
        presenter: GetUserOutputBoundary,
    ) {
        val findUserResult = userDataSourceGateway.findUser(username)
        if (findUserResult.isFailure) {
            presenter.presentUserNotFound("User not found")
            return
        }
        val user =
            findUserResult.getOrNull() ?: run {
                presenter.presentUserNotFound("User not found")
                return
            }
        val tokenResponseModel = UserInfoResponse(user.username, user.createdAt)
        presenter.presentSuccess(tokenResponseModel)
    }
}
