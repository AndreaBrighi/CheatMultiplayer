package org.example.server.interfaceAdaptersLayer.persistence

import org.example.server.interfaceAdaptersLayer.persistence.dao.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByName(name: String): UserEntity?

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM UserEntity u
        WHERE u.name = :name
    """)
    fun test(name: String): Boolean
}
