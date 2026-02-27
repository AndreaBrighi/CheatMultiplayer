package org.example.server.interfaceAdaptersLayer.persistence.dao

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "USERS")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    var id: Long = 0L,
    @Column(name = "NAME", nullable = false)
    val name: String = "",
    @Column(name = "PASSWORD", nullable = false)
    val password: String = "",
    @Column(name = "CREATED_AT")
    val createdAt: LocalDateTime? = null,
)
