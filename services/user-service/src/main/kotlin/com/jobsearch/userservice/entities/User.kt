package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "users")
class User(
    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    val userId: UUID? = null,
    @Column(nullable = false)
    var email: String = "",
    @Column(nullable = false)
    var firstName: String = "",
    @Column(nullable = false)
    var lastName: String = "",
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.APPLICANT,
    @Column(nullable = true)
    var companyId: UUID? = null,
    @Column(nullable = false)
    var isEnabled: Boolean = false,
    @Column(nullable = false)
    var isEmailVerified: Boolean = false,
    @Column(nullable = false)
    var isEmployeeVerified: Boolean = false,
    @Column(nullable = false)
    var createdAt: Timestamp = Timestamp(0),
    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
    ) {
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