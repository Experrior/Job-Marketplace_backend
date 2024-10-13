package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "user_profiles")
class UserProfile (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", updatable = false, nullable = false)
    var profileId: UUID? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    var user: User = User(),
    @Column(nullable = false)
    var resumePath: String = "",
    @Column(nullable = false)
    var profilePicturePath: String = "",
    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
    ) {
    @PrePersist
    fun onCreate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        updatedAt = currentTimestamp
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}