package org.example.server.infrastructure.persistence

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.example.server.application.ports.models.user.UserDataSourceRequestModel
import org.example.server.infrastructure.persistence.entity.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
class UserRegisterDataSourceGatewayImplTest : FunSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var userRepository: UserRepository

    private lateinit var gateway: UserRegisterDataSourceGatewayImpl

    init {

        beforeTest {
            gateway = UserRegisterDataSourceGatewayImpl(userRepository)
        }

        test("existsByName returns false when user absent and true when present") {

            gateway.existsByName("noone").shouldBeFalse()

            val now = LocalDateTime.now()
            val savedId =
                userRepository
                    .save(
                        UserEntity(
                            name = "john",
                            password = "p",
                            createdAt = now,
                        ),
                    ).id

            gateway.existsByName("john").shouldBeTrue()
            (savedId > 0).shouldBeTrue()
        }

        test("save persists user and returns id") {

            val now = LocalDateTime.now()
            val req = UserDataSourceRequestModel("mary", "pwd", now)

            val res = gateway.save(req)
            res.isSuccess.shouldBeTrue()

            val id = res.getOrNull()
            id.shouldNotBeNull()
            (id > 0).shouldBeTrue()

            val persisted = userRepository.findById(id).orElse(null)
            persisted.shouldNotBeNull()
            persisted.name shouldBe "mary"
        }

        test("findUser returns the expected model or failure") {

            val now = LocalDateTime.now()
            val entity =
                UserEntity(
                    name = "anna",
                    password = "hashpass",
                    createdAt = now,
                )
            userRepository.save(entity)

            val findRes = gateway.findUser("anna")
            findRes.isSuccess.shouldBeTrue()

            val model = findRes.getOrNull()
            model.shouldNotBeNull()
            model.name shouldBe "anna"
            model.password shouldBe "hashpass"

            val missing = gateway.findUser("unknown")
            missing.isFailure.shouldBeTrue()
        }
    }
}
