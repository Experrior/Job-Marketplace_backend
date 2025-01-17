package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.time.YearMonth
import java.util.*

@Entity(name = "user_education")
data class Education(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "education_id", updatable = false, nullable = false)
    var educationId: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id", nullable = false)
    var userProfile: UserProfile = UserProfile(),

    @Column(name = "institution_name", nullable = false)
    var institutionName: String = "",

    @Column(nullable = false)
    var degree: String = "",

    @Column(name = "start_date", nullable = false)
    var startDate: YearMonth = YearMonth.now(),

    @Column(name = "end_date", nullable = false)
    var endDate: YearMonth = YearMonth.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
) {
    @PreUpdate
    fun onUpdate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        updatedAt = currentTimestamp
    }
}
