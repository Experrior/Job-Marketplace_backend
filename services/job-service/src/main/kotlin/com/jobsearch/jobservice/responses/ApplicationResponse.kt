package com.jobsearch.jobservice.responses

import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import java.util.*

data class ApplicationResponse(
    val applicationId: UUID,
    val userId: UUID,
    val job: JobResponse,
    val status: ApplicationStatus,
    val resumeUrl: String?,
    val quizResult: QuizResultResponse?,
    val createdAt: Date,
    val updatedAt: Date?
)
