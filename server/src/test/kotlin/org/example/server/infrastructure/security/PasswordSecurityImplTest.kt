package org.example.server.infrastructure.security

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull

class PasswordSecurityImplTest :
    FunSpec({
        val security = PasswordSecurityImpl()

        test("hash and matches behave correctly") {
            val password = "s3cr3t"
            val hash = security.hash(password)
            hash.shouldNotBeNull()

            security.matches(password, hash).shouldBeTrue()
            security.matches("wrong", hash).shouldBeFalse()
        }
    })
