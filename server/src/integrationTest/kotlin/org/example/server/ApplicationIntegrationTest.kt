package org.example.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.example.server.adapters.controllers.dto.LoginRequestDto
import org.example.server.adapters.controllers.dto.LoginResponseDto
import org.example.server.application.ports.PasswordSecurity
import org.example.server.application.ports.TokenSecurity
import org.example.server.infrastructure.persistence.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@Testcontainers
@ActiveProfiles("container")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApplicationIntegrationTest : FunSpec() {
    override fun extensions() = listOf(SpringExtension)

    companion object {
        @Container
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:18.1")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")

        init {
            postgres.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
        }
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var tokenSecurity: TokenSecurity

    @Autowired
    private lateinit var passwordSecurity: PasswordSecurity

    @Autowired
    lateinit var objectMapper: ObjectMapper

    init {
        afterTest {
            jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
        }

        test("login end-to-end with pre-inserted user") {
            val username = "e2euser"
            val rawPassword = "strongpass"

            // prepare user in DB
            val hashed = passwordSecurity.hash(rawPassword)
            val now = LocalDateTime.now()
            jdbcTemplate.update(
                """
                INSERT INTO users (name, password, created_at)
                VALUES (?, ?, ?)
                """.trimIndent(),
                username,
                hashed,
                now,
            )

            // call login endpoint
            val loginReq = LoginRequestDto(username, rawPassword)
            val loginResp =
                restTemplate.postForEntity<LoginResponseDto>(
                    "/api/login",
                    loginReq,
                )

            loginResp.statusCode shouldBe HttpStatus.OK
            loginResp.body shouldNotBe null
            val body = loginResp.body!!
            body.name shouldBe username
            body.token.isNotBlank() shouldBe true
        }
    }
}
