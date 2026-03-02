package org.example.server.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long = 0L,
    @Column(nullable = false)
    val username: String = "",
    @Column(nullable = false)
    val password: String = "",
    @Column(name = "created_at", nullable = true)
    val createdAt: LocalDateTime? = null,
)
