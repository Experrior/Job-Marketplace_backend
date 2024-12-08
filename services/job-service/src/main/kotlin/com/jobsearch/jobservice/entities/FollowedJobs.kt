package com.jobsearch.jobservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "followed_jobs")
@IdClass(UserJobId::class)
data class FollowedJobs(
    @Id
    @Column(nullable = false)
    var userId: UUID,

    @Id
    @Column(name = "job_id", nullable = false, insertable = false, updatable = false)
    var jobId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", referencedColumnName = "job_id", insertable = false, updatable = false)
    var job: Job? = null,

    @Column(name = "created_at",nullable = false)
    var createdAt: Timestamp = Timestamp(0),
) {
    constructor() : this(
        userId = UUID.randomUUID(),
        jobId = UUID.randomUUID(),
        job = null,
        createdAt = Timestamp(0),
    )

    @PrePersist
    fun onCreate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        createdAt = currentTimestamp
    }
}

