package org.example.server.interfaceAdaptersLayer.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.example.server.businessLayer.boundaries.UserSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Duration
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

class UserSecurityImpl(
    key: String,
) : UserSecurity {
    private val signInKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key))
    private val tokenDuration = Duration.ofDays(1)
    private val encoder = BCryptPasswordEncoder()

    override fun getHash(password: String): String = encoder.encode(password)!!

    override fun checkPassword(
        password: String,
        hash: String,
    ): Boolean = encoder.matches(password, hash)

    override fun generateToken(username: String): String {
        val now = Instant.now()
        val expiration = now.plus(tokenDuration)
        val token: String =
            Jwts
                .builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signInKey, Jwts.SIG.HS256)
                .compact()
        return token
    }

    override fun validateToken(token: String): Result<Boolean> =
        try {
            Result.success(!isTokenExpired(token))
        } catch (e: ExpiredJwtException) {
            Result.success(false)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Invalid token"))
        }

    override fun tokenUser(token: String): String = extractUsername(token)

    private fun extractUsername(token: String) = extractAllClaims(token).subject

    private fun extractExpiration(token: String) = extractAllClaims(token).expiration

    private fun isTokenExpired(token: String): Boolean = extractExpiration(token).before(Date.from(Instant.now()))

    private fun extractAllClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(signInKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
