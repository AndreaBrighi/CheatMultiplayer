package org.example.server.interfaceAdaptersLayer.persistence

import org.example.server.businessLayer.adapter.user.UserDataSourceRequestModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
class UserRegisterDataSourceGatewayImplTest(
    @Autowired
    private val userRepository: UserRepository,
) {
    private val gateway = UserRegisterDataSourceGatewayImpl(userRepository)

    @Test
    fun `existsByName returns false when user absent and true when present`() {
        // ensure empty
        assertFalse(gateway.existsByName("noone"))

        // save directly via repository
        val now = LocalDateTime.now()
        val savedId =
            userRepository
                .save(
                    org.example.server.interfaceAdaptersLayer.persistence.dao.UserEntity(
                        name = "john",
                        password = "p",
                        createdAt = now,
                    ),
                ).id

        assertTrue(gateway.existsByName("john"))
        assertTrue(savedId > 0)
    }

    @Test
    fun `save persists user and returns id`() {
        val now = LocalDateTime.now()
        val req = UserDataSourceRequestModel("mary", "pwd", now)

        val res = gateway.save(req)
        assertTrue(res.isSuccess)
        val id = res.getOrNull()
        assertNotNull(id)
        assertTrue(id!! > 0)

        val persisted = userRepository.findById(id).orElse(null)
        assertNotNull(persisted)
        assertEquals("mary", persisted!!.name)
    }

    @Test
    fun `findUser returns the expected LoginDataSourceResponseModel or failure`() {
        val now = LocalDateTime.now()
        val entity =
            org.example.server.interfaceAdaptersLayer.persistence.dao.UserEntity(
                name = "anna",
                password = "hashpass",
                createdAt = now,
            )
        userRepository.save(entity)

        val findRes = gateway.findUser("anna")
        assertTrue(findRes.isSuccess)
        val model = findRes.getOrNull()
        assertNotNull(model)
        assertEquals("anna", model!!.name)
        assertEquals("hashpass", model.password)

        val missing = gateway.findUser("unknown")
        assertTrue(missing.isFailure)
    }
}
