package org.example.server.businessLayer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.server.businessLayer.adapter.login.LoginDataSourceResponseModel
import org.example.server.businessLayer.adapter.login.LoginRequestModel
import org.example.server.businessLayer.adapter.user.UserDataSourceRequestModel
import org.example.server.businessLayer.adapter.user.UserRequestModel
import org.example.server.businessLayer.boundaries.UserRegisterDataSourceGateway
import org.example.server.businessLayer.boundaries.UserSecurity
import org.example.server.businessLayer.exception.PasswordToShortException
import org.example.server.businessLayer.exception.UserAlreadyPresentException
import org.example.server.businessLayer.exception.UserNotFound

class UserRegisterUseCaseTest :
    FunSpec({
        val userDataSourceGateway: UserRegisterDataSourceGateway = mockk()
        val userSecurity: UserSecurity = mockk()

        val useCase = UserRegisterUseCase(userDataSourceGateway, userSecurity)

        afterTest {
            clearAllMocks()
        }

        test("createUser success returns UserResponseModel") {
            val request = UserRequestModel(name = "bob", password = "strongpass")

            every { userDataSourceGateway.existsByName(request.name) } returns false
            every { userSecurity.getHash(request.password) } returns "hashed"
            every { userDataSourceGateway.save(any<UserDataSourceRequestModel>()) } returns Result.success(1L)
            every { userSecurity.generateToken(request.name) } returns "tok123"

            val result = useCase.createUser(request)

            result.isSuccess shouldBe true
            val value = result.getOrNull()
            value shouldNotBe null
            value!!.name shouldBe "bob"
            value.token shouldBe "tok123"

            verify { userDataSourceGateway.existsByName("bob") }
            verify { userSecurity.getHash("strongpass") }
            verify {
                userDataSourceGateway.save(
                    match {
                        it.name == "bob" &&
                            it.password == "hashed"
                    },
                )
            }
            verify { userSecurity.generateToken("bob") }
        }

        test("createUser duplicate returns UserAlreadyPresentException") {
            val request = UserRequestModel(name = "bob", password = "strongpass")

            every { userDataSourceGateway.existsByName(request.name) } returns true

            val result = useCase.createUser(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex.shouldBeInstanceOf<UserAlreadyPresentException>()

            verify(exactly = 0) { userDataSourceGateway.save(any()) }
        }

        test("createUser short password returns PasswordToShortException") {
            val request = UserRequestModel(name = "bob", password = "short")

            every { userDataSourceGateway.existsByName(request.name) } returns false

            val result = useCase.createUser(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex.shouldBeInstanceOf<PasswordToShortException>()

            verify(exactly = 0) { userDataSourceGateway.save(any()) }
        }

        test("createUser save failure returns underlying exception") {
            val request = UserRequestModel(name = "bob", password = "strongpass")

            every { userDataSourceGateway.existsByName(request.name) } returns false
            every { userSecurity.getHash(request.password) } returns "hashed"
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
                    LoginDataSourceResponseModel(name = "alice", password = "hashedpwd"),
                )
            every { userSecurity.checkPassword(request.password, "hashedpwd") } returns true
            every { userSecurity.generateToken(request.username) } returns "tok-abc"

            val result = useCase.login(request)

            result.isSuccess shouldBe true
            val value = result.getOrNull()
            value shouldNotBe null
            value!!.name shouldBe "alice"
            value.token shouldBe "tok-abc"

            verify { userDataSourceGateway.findUser(request.username) }
            verify { userSecurity.checkPassword(request.password, "hashedpwd") }
            verify { userSecurity.generateToken(request.username) }
        }

        test("login user not found returns UserNotFound") {
            val request = LoginRequestModel(username = "alice", password = "pwd")

            every { userDataSourceGateway.findUser(request.username) } returns Result.failure(Exception("not found"))

            val result = useCase.login(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex.shouldBeInstanceOf<UserNotFound>()

            verify(exactly = 0) { userSecurity.checkPassword(any(), any()) }
            verify(exactly = 0) { userSecurity.generateToken(any()) }
        }

        test("login wrong password returns Invalid password exception") {
            val request = LoginRequestModel(username = "alice", password = "pwd")

            every { userDataSourceGateway.findUser(request.username) } returns
                Result.success(
                    LoginDataSourceResponseModel(name = "alice", password = "hashedpwd"),
                )
            every { userSecurity.checkPassword(request.password, "hashedpwd") } returns false

            val result = useCase.login(request)

            result.isFailure shouldBe true
            val ex = result.exceptionOrNull()
            ex shouldNotBe null
            ex!!.message shouldBe "Invalid password"

            verify { userDataSourceGateway.findUser(request.username) }
            verify { userSecurity.checkPassword(request.password, "hashedpwd") }
            verify(exactly = 0) { userSecurity.generateToken(any()) }
        }
    })
