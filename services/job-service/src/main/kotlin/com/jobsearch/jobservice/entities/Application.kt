package com.jobsearch.jobservice.entities

import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
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

    @Column(name = "s3_resume_path", nullable = false)
    var s3ResumePath : String? = null,

    @Transient
    var resumeUrl: String? = null,

    @Column(name = "status", nullable = false)
    var status: ApplicationStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_result_id", referencedColumnName = "quiz_result_id", nullable = true)
    var quizResult: QuizResult? = null,

    @Transient
    var fullName: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp.from(Instant.now()),

    @Column(name = "updated_at", nullable = true)
    var updatedAt: Timestamp? = null,
) {
    constructor() : this(
        applicationId = null,
        userId = UUID.randomUUID(),
        job = Job(),
        s3ResumePath = null,
        status = ApplicationStatus.PENDING
    )

    @PrePersist
    fun onCreate() {
        createdAt = Timestamp.from(Instant.now())
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}
