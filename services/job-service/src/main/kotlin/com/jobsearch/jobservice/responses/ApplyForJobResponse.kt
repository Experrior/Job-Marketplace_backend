package com.jobsearch.jobservice.responses

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import java.util.*

data class ApplyForJobResponse(
    val applicationId: UUID,
    val userId: UUID,
    val job: Job,
    val applicationDate: Date,
    val status: ApplicationStatus,
)
