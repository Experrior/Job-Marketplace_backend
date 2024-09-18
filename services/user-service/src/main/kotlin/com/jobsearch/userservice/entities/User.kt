package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "userId", updatable = false, nullable = false)
    val userId: UUID? = null,
    @Column(nullable = false, unique = true)
    var auth0Id: String = "",
    @Column(nullable = true)
    var companyId: UUID? = null,
    @Column(nullable = false)
    var email: String = "",
    @Column(nullable = false)
    var firstName: String = "",
    @Column(nullable = false)
    var lastName: String = "",
    @Column(nullable = true)
    var phone: String = "",
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.APPLICANT,
    @Column(nullable = false)
    var isBlocked: Boolean = false,
    @Column(nullable = false)
    var emailVerified: Boolean = false,
    @Column(nullable = false)
    var employeeVerified: Boolean = false,
    @Column(nullable = false)
    var createdAt: Timestamp = Timestamp(0),
    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
    )
    {
    @PrePersist
    fun onCreate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        createdAt = currentTimestamp
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}