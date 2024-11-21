package com.jobsearch.userservice.responses

import java.sql.Timestamp
import java.util.*

data class ResumeResponse(
    val resumeId: UUID,
    val resumeName: String,
    val resumeUrl: String,
    val createdAt: Timestamp
)