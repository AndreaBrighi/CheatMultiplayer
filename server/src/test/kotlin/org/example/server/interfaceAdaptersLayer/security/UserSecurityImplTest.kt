package org.example.server.interfaceAdaptersLayer.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.Date

class UserSecurityImplTest :
    FunSpec({
        val rawKey = "01234567890123456789012345678901" // 32 bytes
        val base64Key = Base64.getEncoder().encodeToString(rawKey.toByteArray())
        val security = UserSecurityImpl(base64Key)

        test("getHash produces a hash and checkPassword validates it") {
            val password = "mySecretPass"
            val hash = security.getHash(password)
            hash.shouldNotBeNull()
            security.checkPassword(password, hash).shouldBeTrue()
            security.checkPassword("wrongPass", hash).shouldBeFalse()
        }

        test("generateToken returns a valid token that can be validated and yields username") {
            val username = "alice"
            val token = security.generateToken(username)
            token.shouldNotBeNull()

            val validate = security.isTokenValid(token)
            validate.isSuccess shouldBe true
            validate.getOrNull() shouldBe true

            val extracted = security.tokenUser(token)
            extracted shouldBe username
        }

        test("isTokenValid returns failure for malformed token") {
            val res = security.isTokenValid("not-a-token")
            res.isFailure.shouldBeTrue() // expecting an IllegalArgumentException
            val ex = res.exceptionOrNull()
            ex.shouldNotBeNull()
            ex is IllegalArgumentException
            (ex.message) shouldBe "Invalid token"
        }

        test("isTokenValid returns success false for expired token") {
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
            res.isSuccess shouldBe true
            res.getOrNull() shouldBe false
        }
    })
