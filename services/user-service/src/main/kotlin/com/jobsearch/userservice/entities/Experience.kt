package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.time.YearMonth
import java.util.*

@Entity(name = "user_experience")
class Experience(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "experience_id", updatable = false, nullable = false)
    var experienceId: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    var userProfile: UserProfile = UserProfile(),

    @Column(name = "company_name", nullable = false)
    var companyName: String = "",

    @Column(nullable = false)
    var role: String = "",

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
