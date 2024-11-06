package com.jobsearch.jobservice.entities

import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity(name = "applications")
data class Application(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_id", updatable = false, nullable = false)
    var applicationId: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @ManyToOne
    @JoinColumn(name = "job_id", referencedColumnName = "job_id", nullable = false)
    var job: Job,

    @Lob
    @Column(name = "resume", nullable = true)
    var resume: ByteArray? = null,

    @Column(name = "application_date", nullable = false)
    var applicationDate: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "status", nullable = false)
    var status: ApplicationStatus
) {
    constructor() : this(
        applicationId = null,
        userId = UUID.randomUUID(),
        job = Job(),
        resume = null,
        applicationDate = Timestamp(System.currentTimeMillis()),
        status = ApplicationStatus.PENDING
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Application

        if (resume != null) {
            if (other.resume == null) return false
            if (!resume.contentEquals(other.resume)) return false
        } else if (other.resume != null) return false

        return true
    }

    override fun hashCode(): Int {
        return resume?.contentHashCode() ?: 0
    }
}
