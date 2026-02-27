package org.example.server

import org.example.server.interfaceAdaptersLayer.persistence.UserRepository
import org.example.server.interfaceAdaptersLayer.persistence.dao.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.PostgreSQLContainer
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationIntegrationTest {
    companion object {
        private var postgres: PostgreSQLContainer<*>? = null

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            // Allow overriding the Docker availability check via env var (useful locally/CI)
            val forceTestcontainers = System.getenv("FORCE_TESTCONTAINERS")?.toBoolean() ?: false

            var dockerAvailable = false
            try {
                dockerAvailable = DockerClientFactory.instance().isDockerAvailable
            } catch (e: Throwable) {
                // ignore - we'll decide based on force flag
            }

            if (!dockerAvailable && forceTestcontainers) {
                // If forced, attempt to use Docker anyway (start will be attempted below and may fail)
                dockerAvailable = true
            }

            if (dockerAvailable) {
                try {
                    postgres =
                        PostgreSQLContainer("postgres:18.1")
                            .withDatabaseName("testdb")
                            .withUsername("test")
                            .withPassword("test")
                    postgres!!.start()

                    // If start succeeded, register Postgres properties
                    registry.add("spring.datasource.url") { postgres!!.jdbcUrl }
                    registry.add("spring.datasource.username") { postgres!!.username }
                    registry.add("spring.datasource.password") { postgres!!.password }
                    registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
                    registry.add("spring.jpa.hibernate.ddl-auto") { "update" }
                    return
                } catch (e: Throwable) {
                    // Starting container failed even though we attempted it (Docker might be misconfigured)
                    // Fall back to H2 below
                    println("[Test Setup] Testcontainers requested but failed to start. Falling back to H2. Error: ${e.message}")
                }
            }

            // Fallback to H2 in-memory when Docker is not available (CI/local dev without Docker)
            registry.add("spring.datasource.url") { "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false" }
            registry.add("spring.datasource.username") { "sa" }
            registry.add("spring.datasource.password") { "" }
            registry.add("spring.datasource.driver-class-name") { "org.h2.Driver" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.jpa.database-platform") { "org.hibernate.dialect.H2Dialect" }
        }
    }

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userSecurity: org.example.server.businessLayer.boundaries.UserSecurity

    @Test
    fun `login end-to-end with pre-inserted user`() {
        val username = "e2euser"
        val rawPassword = "strongpass"

        // prepare user in DB
        val hashed = userSecurity.getHash(rawPassword)
        val now = LocalDateTime.now()
        val entity = UserEntity(name = username, password = hashed, createdAt = now)
        userRepository.save(entity)

        // call login endpoint
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val loginReq = mapOf("username" to username, "password" to rawPassword)
        val loginResp =
            restTemplate.postForEntity<String>("http://localhost:$port/api/login", HttpEntity(loginReq, headers))
        assertEquals(HttpStatus.OK, loginResp.statusCode)
    }
}
