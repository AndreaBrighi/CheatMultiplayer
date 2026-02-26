package org.example.server.interfaceAdaptersLayer.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.Date

class UserSecurityImplTest {
    private val rawKey = "01234567890123456789012345678901" // 32 bytes
    private val base64Key = Base64.getEncoder().encodeToString(rawKey.toByteArray())
    private val security = UserSecurityImpl(base64Key)

    @Test
    fun `getHash produces a hash and checkPassword validates it`() {
        val password = "mySecretPass"
        val hash = security.getHash(password)
        assertNotNull(hash)
        assertTrue(security.checkPassword(password, hash))
        assertFalse(security.checkPassword("wrongPass", hash))
    }

    @Test
    fun `generateToken returns a valid token that can be validated and yields username`() {
        val username = "alice"
        val token = security.generateToken(username)
        assertNotNull(token)

        val validate = security.isTokenValid(token)
        assertTrue(validate.isSuccess)
        assertTrue(validate.getOrNull()!!)

        val extracted = security.tokenUser(token)
        assertEquals(username, extracted)
    }

    @Test
    fun `isTokenValid returns failure for malformed token`() {
        val res = security.isTokenValid("not-a-token")
        assertTrue(res.isFailure)
        val ex = res.exceptionOrNull()
        assertNotNull(ex)
        assertTrue(ex is IllegalArgumentException)
        assertEquals("Invalid token", ex!!.message)
    }

    @Test
    fun `isTokenValid returns success false for expired token`() {
        // Build an expired token signed with the same key
        val keyBytes = Decoders.BASE64.decode(base64Key)
        val key = Keys.hmacShaKeyFor(keyBytes)

        val now = Instant.now()
        val issued = Date.from(now.minus(Duration.ofDays(2)))
        val expiredAt = Date.from(now.minus(Duration.ofDays(1)))

        val expiredToken =
            Jwts
                .builder()
                .subject("bob")
                .issuedAt(issued)
                .expiration(expiredAt)
                .signWith(key, Jwts.SIG.HS256)
                .compact()

        val res = security.isTokenValid(expiredToken)
        assertTrue(res.isSuccess)
        assertFalse(res.getOrNull()!!)
    }
}
