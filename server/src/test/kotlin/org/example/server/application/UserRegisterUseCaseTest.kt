package org.example.server.application

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.server.application.ports.CreateUserOutputBoundary
import org.example.server.application.ports.GetUserOutputBoundary
import org.example.server.application.ports.LoginOutputBoundary
import org.example.server.application.ports.PasswordSecurity
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserRegisterDataSourceGateway
import org.example.server.application.ports.models.login.LoginDataSourceResponseModel
import org.example.server.application.ports.models.login.LoginRequestModel
import org.example.server.application.ports.models.user.UserDataSourceRequestModel
import org.example.server.application.ports.models.user.UserRequestModel
import org.example.server.application.usecases.UserRegisterUseCase
import java.time.LocalDateTime

class UserRegisterUseCaseTest :
    FunSpec({
        val userDataSourceGateway: UserRegisterDataSourceGateway = mockk()
        val passwordSecurity: PasswordSecurity = mockk()
        val tokenSecurity: TokenSecurity = mockk()

        val useCase = UserRegisterUseCase(userDataSourceGateway, passwordSecurity, tokenSecurity)

        afterTest {
            clearAllMocks()
        }

        test("createUser success returns UserResponseModel") {
            val request = UserRequestModel(username = "bob", password = "strongpass")

            every { userDataSourceGateway.existsByUsername(request.username) } returns false
            every { passwordSecurity.hash(request.password) } returns "hashed"
            every { userDataSourceGateway.save(any<UserDataSourceRequestModel>()) } returns Result.success(1L)
            every { tokenSecurity.generateToken(request.username) } returns "tok123"

            val presenter = mockk<CreateUserOutputBoundary>(relaxed = true)

            useCase.createUser(request, presenter)

            verify { userDataSourceGateway.existsByUsername("bob") }
            verify { passwordSecurity.hash("strongpass") }
            verify {
                userDataSourceGateway.save(
                    match {
                        it.username == "bob" &&
                            it.password == "hashed"
                    },
                )
            }
            verify { tokenSecurity.generateToken("bob") }

            // verify presenter called with expected response
            verify { presenter.presentSuccess(match { it.username == "bob" && it.token == "tok123" }) }
        }

        test("createUser duplicate returns UserAlreadyPresentException") {
            val request = UserRequestModel(username = "bob", password = "strongpass")

            every { userDataSourceGateway.existsByUsername(request.username) } returns true

            val presenter = mockk<CreateUserOutputBoundary>(relaxed = true)
            useCase.createUser(request, presenter)

            verify { userDataSourceGateway.existsByUsername(request.username) }
            verify(exactly = 0) { userDataSourceGateway.save(any()) }

            // verify presenter received the correct error call
            verify { presenter.presentUserAlreadyExists(any()) }
        }

        test("createUser short password returns PasswordToShortException") {
            val request = UserRequestModel(username = "bob", password = "short")

            every { userDataSourceGateway.existsByUsername(request.username) } returns false

            val presenter = mockk<CreateUserOutputBoundary>(relaxed = true)
            useCase.createUser(request, presenter)

            verify { userDataSourceGateway.existsByUsername(request.username) }
            verify(exactly = 0) { userDataSourceGateway.save(any()) }

            verify { presenter.presentPasswordError("Password too short") }
        }

        test("createUser save failure returns underlying exception") {
            val request = UserRequestModel(username = "bob", password = "strongpass")

            every { userDataSourceGateway.existsByUsername(request.username) } returns false
            every { passwordSecurity.hash(request.password) } returns "hashed"
            every { userDataSourceGateway.save(any<UserDataSourceRequestModel>()) } returns Result.failure(Exception("db error"))

            val presenter = mockk<CreateUserOutputBoundary>(relaxed = true)
            useCase.createUser(request, presenter)

            verify { userDataSourceGateway.existsByUsername(request.username) }
            verify { passwordSecurity.hash(request.password) }
            verify { userDataSourceGateway.save(any<UserDataSourceRequestModel>()) }

            verify { presenter.presentSaveError(any()) }
        }

        test("login success returns token") {
            val request = LoginRequestModel(username = "alice", password = "pwd")

            every { userDataSourceGateway.findUser(request.username) } returns
                Result.success(
                    LoginDataSourceResponseModel(
                        username = "alice",
                        password = "hashedpwd",
                        createdAt = LocalDateTime.now(),
                    ),
                )
            every { passwordSecurity.matches(request.password, "hashedpwd") } returns true
            every { tokenSecurity.generateToken(request.username) } returns "tok-abc"

            val presenter = mockk<LoginOutputBoundary>(relaxed = true)
            useCase.login(request, presenter)

            verify { userDataSourceGateway.findUser(request.username) }
            verify { passwordSecurity.matches(request.password, "hashedpwd") }
            verify { tokenSecurity.generateToken(request.username) }

            verify { presenter.presentSuccess(match { it.token == "tok-abc" }) }
        }

        test("login user not found returns UserNotFound") {
            val request = LoginRequestModel(username = "alice", password = "pwd")

            every { userDataSourceGateway.findUser(request.username) } returns Result.failure(Exception("not found"))

            val presenter = mockk<LoginOutputBoundary>(relaxed = true)
            useCase.login(request, presenter)

            verify(exactly = 0) { passwordSecurity.matches(any(), any()) }
            verify(exactly = 0) { tokenSecurity.generateToken(any()) }

            verify { presenter.presentUserNotFound("User not found") }
        }

        test("login wrong password returns Invalid password exception") {
            val request = LoginRequestModel(username = "alice", password = "pwd")

            every { userDataSourceGateway.findUser(request.username) } returns
                Result.success(
                    LoginDataSourceResponseModel(username = "alice", password = "hashedpwd", LocalDateTime.now()),
                )
            every { passwordSecurity.matches(request.password, "hashedpwd") } returns false

            val presenter = mockk<LoginOutputBoundary>(relaxed = true)
            useCase.login(request, presenter)

            verify { userDataSourceGateway.findUser(request.username) }
            verify { passwordSecurity.matches(request.password, "hashedpwd") }
            verify(exactly = 0) { tokenSecurity.generateToken(any()) }

            verify { presenter.presentInvalidCredentials(any()) }
        }

        test("get user by username success returns UserResponseModel") {
            val username = "alice"
            val now = LocalDateTime.now()

            every { userDataSourceGateway.findUser(username) } returns
                Result.success(
                    LoginDataSourceResponseModel(
                        username = username,
                        password = "hashedpwd",
                        createdAt = now,
                    ),
                )

            val presenter = mockk<GetUserOutputBoundary>(relaxed = true)
            useCase.getUser(username, presenter)

            verify { userDataSourceGateway.findUser(username) }

            verify { presenter.presentSuccess(match { it.username == username && it.createdAt == now }) }
        }
    })
