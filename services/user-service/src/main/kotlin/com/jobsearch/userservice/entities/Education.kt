package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "user_education")
class Education(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "institution_id", updatable = false, nullable = false)
    var institutionId: UUID? = null,
    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    var userProfileId: UserProfile = UserProfile(),
    @Column(nullable = false)
    var institutionName: String = "",
    @Column(nullable = false)
    var degree: String = "",
    @Column(nullable = false)
    var startDate: Timestamp = Timestamp(0),
    @Column(nullable = false)
    var endDate: Timestamp = Timestamp(0),
    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
    ) {
    @PreUpdate
    fun onUpdate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        updatedAt = currentTimestamp
    }
}