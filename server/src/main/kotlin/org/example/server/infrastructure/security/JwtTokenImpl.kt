package org.example.server.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.example.server.application.ports.TokenSecurity
import java.time.Duration
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

class JwtTokenImpl(
    key: String,
) : TokenSecurity {
    private val signInKey: SecretKey =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(key))

    private val tokenDuration = Duration.ofDays(1)

    override fun generateToken(username: String): String {
        val now = Instant.now()
        val expiration = now.plus(tokenDuration)

        return Jwts
            .builder()
            .subject(username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(signInKey, Jwts.SIG.HS256)
            .compact()
    }

    override fun isTokenValid(token: String): Result<Boolean> =
        try {
            Result.success(!isTokenExpired(token))
        } catch (_: ExpiredJwtException) {
            Result.success(false)
        } catch (_: Exception) {
            Result.failure(IllegalArgumentException("Invalid token"))
        }

    override fun extractUsername(token: String): String = extractAllClaims(token).subject

    private fun isTokenExpired(token: String): Boolean =
        extractAllClaims(token)
            .expiration
            .before(Date.from(Instant.now()))

    private fun extractAllClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(signInKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
