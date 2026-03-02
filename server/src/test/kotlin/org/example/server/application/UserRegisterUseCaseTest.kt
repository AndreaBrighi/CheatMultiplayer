package org.example.server.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.server.application.ports.PasswordSecurity
import org.example.server.application.ports.TokenSecurity
import org.example.server.application.ports.UserRegisterDataSourceGateway
import org.example.server.application.ports.models.exception.PasswordToShortException
import org.example.server.application.ports.models.exception.UserAlreadyPresentException
import org.example.server.application.ports.models.exception.UserNotFound
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

            val result = useCase.createUser(request)

            result.isSuccess shouldBe true
            val value = result.getOrNull()
            value shouldNotBe null
            value!!.username shouldBe "bob"
            value.token shouldBe "tok123"

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
        }

        test("createUser duplicate returns UserAlreadyPresentException") {
            val request = UserRequestModel(username = "bob", password = "strongpass")

            every { userDataSourceGateway.existsByUsername(request.username) } returns true

            val result = useCase.createUser(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex.shouldBeInstanceOf<UserAlreadyPresentException>()

            verify(exactly = 0) { userDataSourceGateway.save(any()) }
        }

        test("createUser short password returns PasswordToShortException") {
            val request = UserRequestModel(username = "bob", password = "short")

            every { userDataSourceGateway.existsByUsername(request.username) } returns false

            val result = useCase.createUser(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex.shouldBeInstanceOf<PasswordToShortException>()

            verify(exactly = 0) { userDataSourceGateway.save(any()) }
        }

        test("createUser save failure returns underlying exception") {
            val request = UserRequestModel(username = "bob", password = "strongpass")

            every { userDataSourceGateway.existsByUsername(request.username) } returns false
            every { passwordSecurity.hash(request.password) } returns "hashed"
            every { userDataSourceGateway.save(any<UserDataSourceRequestModel>()) } returns Result.failure(Exception("db error"))

            val result = useCase.createUser(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex!!.message shouldBe "db error"
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

            val result = useCase.login(request)

            result.isSuccess shouldBe true
            val value = result.getOrNull()
            value shouldNotBe null
            value!!.username shouldBe "alice"
            value.token shouldBe "tok-abc"

            verify { userDataSourceGateway.findUser(request.username) }
            verify { passwordSecurity.matches(request.password, "hashedpwd") }
            verify { tokenSecurity.generateToken(request.username) }
        }

        test("login user not found returns UserNotFound") {
            val request = LoginRequestModel(username = "alice", password = "pwd")

            every { userDataSourceGateway.findUser(request.username) } returns Result.failure(Exception("not found"))

            val result = useCase.login(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex.shouldBeInstanceOf<UserNotFound>()

            verify(exactly = 0) { passwordSecurity.matches(any(), any()) }
            verify(exactly = 0) { tokenSecurity.generateToken(any()) }
        }

        test("login wrong password returns Invalid password exception") {
            val request = LoginRequestModel(username = "alice", password = "pwd")

            every { userDataSourceGateway.findUser(request.username) } returns
                Result.success(
                    LoginDataSourceResponseModel(username = "alice", password = "hashedpwd", LocalDateTime.now()),
                )
            every { passwordSecurity.matches(request.password, "hashedpwd") } returns false

            val result = useCase.login(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex!!.message shouldBe "Invalid password"

            verify { userDataSourceGateway.findUser(request.username) }
            verify { passwordSecurity.matches(request.password, "hashedpwd") }
            verify(exactly = 0) { tokenSecurity.generateToken(any()) }
        }

        test("get user by username success returns UserResponseModel") {
            val username = "alice"

            every { userDataSourceGateway.findUser(username) } returns
                Result.success(
                    LoginDataSourceResponseModel(
                        username = "alice",
                        password = "hashedpwd",
                        createdAt = LocalDateTime.now(),
                    ),
                )

            val result = useCase.getUser(username)

            result.isSuccess shouldBe true
            val value = result.getOrNull()
            value shouldNotBe null
            value!!.username shouldBe "alice"
            value.createdAt shouldNotBe null

            verify { userDataSourceGateway.findUser(username) }
        }
    })
