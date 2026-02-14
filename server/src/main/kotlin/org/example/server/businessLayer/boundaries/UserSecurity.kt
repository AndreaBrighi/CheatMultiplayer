package org.example.server.businessLayer.boundaries

interface UserSecurity {

    fun getHash(password: String): String

    fun checkPassword(password: String, hash: String): Boolean

    fun generateToken(username: String): String

    fun validateToken(token: String): Result<Boolean>

    fun tokenUser(token: String): String
}
