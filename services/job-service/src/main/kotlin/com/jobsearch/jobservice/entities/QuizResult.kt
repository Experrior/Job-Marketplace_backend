package com.jobsearch.jobservice.entities

import com.jobsearch.jobservice.entities.quizzes.Quiz
import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "quiz_results")
data class QuizResult(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "quiz_result_id", updatable = false, nullable = false)
    var quizResultId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    var quiz: Quiz,

    @Column(name = "applicant_id", nullable = false)
    var applicantId: UUID,

    @Column(name = "score", nullable = false)
    var score: Int,

    @Column(name = "time_taken", nullable = true)
    var timeTaken: Int? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp.from(Instant.now()),
    ) {
    constructor() : this(
        quiz = Quiz(),
        applicantId = UUID.randomUUID(),
        score = 0,
    )

    @PrePersist
    fun onCreate() {
        createdAt = Timestamp.from(Instant.now())
    }
}
