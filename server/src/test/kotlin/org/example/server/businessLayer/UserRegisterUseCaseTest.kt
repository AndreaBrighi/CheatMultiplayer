package org.example.server.businessLayer

import org.example.server.businessLayer.adapter.login.LoginDataSourceResponseModel
import org.example.server.businessLayer.adapter.login.LoginRequestModel
import org.example.server.businessLayer.adapter.user.UserDataSourceRequestModel
import org.example.server.businessLayer.adapter.user.UserRequestModel
import org.example.server.businessLayer.boundaries.UserRegisterDataSourceGateway
import org.example.server.businessLayer.boundaries.UserSecurity
import org.example.server.businessLayer.exception.PasswordToShortException
import org.example.server.businessLayer.exception.UserAlreadyPresentException
import org.example.server.businessLayer.exception.UserNotFound
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UserRegisterUseCaseTest {
    private val userDataSourceGateway: UserRegisterDataSourceGateway = mock()
    private val userSecurity: UserSecurity = mock()

    private val useCase = UserRegisterUseCase(userDataSourceGateway, userSecurity)

    @Test
    fun `createUser success returns UserResponseModel`() {
        val request = UserRequestModel(name = "bob", password = "strongpass")

        whenever(userDataSourceGateway.existsByName(request.name)).thenReturn(false)
        whenever(userSecurity.getHash(request.password)).thenReturn("hashed")
        whenever(userDataSourceGateway.save(any<UserDataSourceRequestModel>())).thenReturn(Result.success(1L))
        whenever(userSecurity.generateToken(request.name)).thenReturn("tok123")

        val result = useCase.createUser(request)

        assertTrue(result.isSuccess)
        val value = result.getOrNull()
        assertNotNull(value)
        assertEquals("bob", value!!.name)
        assertEquals("tok123", value.token)
        verify(userDataSourceGateway).existsByName("bob")
        verify(userSecurity).getHash("strongpass")
        verify(userDataSourceGateway).save(any<UserDataSourceRequestModel>())
        verify(userSecurity).generateToken("bob")
    }

    @Test
    fun `createUser duplicate returns UserAlreadyPresentException`() {
        val request = UserRequestModel(name = "bob", password = "strongpass")

        whenever(userDataSourceGateway.existsByName(request.name)).thenReturn(true)

        val result = useCase.createUser(request)

        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue(ex is UserAlreadyPresentException)
        verify(userDataSourceGateway, never()).save(any())
    }

    @Test
    fun `createUser short password returns PasswordToShortException`() {
        val request = UserRequestModel(name = "bob", password = "short")

        whenever(userDataSourceGateway.existsByName(request.name)).thenReturn(false)

        val result = useCase.createUser(request)

        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue(ex is PasswordToShortException)
        verify(userDataSourceGateway, never()).save(any())
    }

    @Test
    fun `createUser save failure returns underlying exception`() {
        val request = UserRequestModel(name = "bob", password = "strongpass")

        whenever(userDataSourceGateway.existsByName(request.name)).thenReturn(false)
        whenever(userSecurity.getHash(request.password)).thenReturn("hashed")
        whenever(userDataSourceGateway.save(any<UserDataSourceRequestModel>())).thenReturn(Result.failure(Exception("db error")))

        val result = useCase.createUser(request)

        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertEquals("db error", ex!!.message)
    }

    @Test
    fun `login success returns token`() {
        val request = LoginRequestModel(username = "alice", password = "pwd")

        whenever(
            userDataSourceGateway.findUser(request.username),
        ).thenReturn(Result.success(LoginDataSourceResponseModel(name = "alice", password = "hashedpwd")))
        whenever(userSecurity.checkPassword(request.password, "hashedpwd")).thenReturn(true)
        whenever(userSecurity.generateToken(request.username)).thenReturn("tok-abc")

        val result = useCase.login(request)

        assertTrue(result.isSuccess)
        val value = result.getOrNull()
        assertNotNull(value)
        assertEquals("alice", value!!.name)
        assertEquals("tok-abc", value.token)
        verify(userDataSourceGateway).findUser(request.username)
        verify(userSecurity).checkPassword(request.password, "hashedpwd")
        verify(userSecurity).generateToken(request.username)
    }

    @Test
    fun `login user not found returns UserNotFound`() {
        val request = LoginRequestModel(username = "alice", password = "pwd")

        whenever(userDataSourceGateway.findUser(request.username)).thenReturn(Result.failure(Exception("not found")))

        val result = useCase.login(request)

        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue(ex is UserNotFound)
        verify(userSecurity, never()).checkPassword(any(), any())
        verify(userSecurity, never()).generateToken(any())
    }

    @Test
    fun `login wrong password returns Invalid password exception`() {
        val request = LoginRequestModel(username = "alice", password = "pwd")

        whenever(
            userDataSourceGateway.findUser(request.username),
        ).thenReturn(Result.success(LoginDataSourceResponseModel(name = "alice", password = "hashedpwd")))
        whenever(userSecurity.checkPassword(request.password, "hashedpwd")).thenReturn(false)

        val result = useCase.login(request)

        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertEquals("Invalid password", ex!!.message)
        verify(userDataSourceGateway).findUser(request.username)
        verify(userSecurity).checkPassword(request.password, "hashedpwd")
        verify(userSecurity, never()).generateToken(any())
    }
}
