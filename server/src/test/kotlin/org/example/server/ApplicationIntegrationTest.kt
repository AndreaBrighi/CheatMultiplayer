package org.example.server

import org.example.server.businessLayer.boundaries.UserSecurity
import org.example.server.interfaceAdaptersLayer.controllers.dto.LoginRequestDto
import org.example.server.interfaceAdaptersLayer.persistence.UserRepository
import org.example.server.interfaceAdaptersLayer.persistence.dao.UserEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.server.LocalServerPort
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
class ApplicationIntegrationTest {
    companion object {
        @Container
        val postgres =
            PostgreSQLContainer("postgres:18.1")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
        }
    }

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var userSecurity: UserSecurity

    @AfterEach
    fun cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
    }

    @Test
    fun `login end-to-end with pre-inserted user`() {
        val username = "e2euser"
        val rawPassword = "strongpass"

        // prepare user in DB
        val hashed = userSecurity.getHash(rawPassword)
        val now = LocalDateTime.now()
        val entity = UserEntity(name = username, password = hashed, createdAt = now)
        userRepository.saveAndFlush(entity)

        // call login endpoint
        val loginReq = LoginRequestDto(username, rawPassword)
        val loginResp =
            restTemplate.postForEntity<String>(
                "http://localhost:$port/api/login",
                loginReq,
            )
        assertEquals(HttpStatus.OK, loginResp.statusCode)
    }
}
