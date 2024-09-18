package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "user_profiles")
class Profile (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profileId", updatable = false, nullable = false)
    var profileId: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    var userId: User = User(),

    @Column(nullable = false)
    var resumePath: String = "",

    @Column(nullable = false)
    var profilePicturePath: String = "",

    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0),

    ) {

    @PrePersist
    fun onCreate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        updatedAt = currentTimestamp
    }
}