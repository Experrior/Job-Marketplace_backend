package com.jobsearch.jobservice.entities.quizzes

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "quizzes")
data class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "quiz_id", updatable = false, nullable = false)
    var quizId: UUID? = null,

    @Column(name = "recruiter_id", nullable = false)
    var recruiterId: UUID? = null,

    @Column(name = "s3_quiz_path", nullable = false)
    var s3QuizPath: String? = null,

    @Transient
    var quizUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp.from(Instant.now()),

    @Column(name = "updated_at", nullable = true)
    var updatedAt: Timestamp? = null,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
) {

    constructor() : this(
        quizId = null,
        s3QuizPath = null,
        createdAt = Timestamp.from(Instant.now()),
        isDeleted = false
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
