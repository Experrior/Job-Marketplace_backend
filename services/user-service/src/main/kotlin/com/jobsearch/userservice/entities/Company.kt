package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "company")
data class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id", updatable = false, nullable = false)
    var companyId: UUID? = null,
    @Column(nullable = false)
    var email: String = "",
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var industry: String = "",
    @Column(nullable = true)
    var description: String,
    @Column(nullable = false)
    var logoPath: String = "",
    @Column(nullable = false)
    var isEmailVerified: Boolean = false,
    @Column(nullable = false)
    var createdAt: Timestamp = Timestamp(0),
    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
) {
    constructor() : this(
        companyId = null,
        email = "",
        name = "",
        industry = "",
        description = "",
        logoPath = "",
        isEmailVerified = false,
        createdAt = Timestamp(0),
        updatedAt = Timestamp(0)
    )

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
