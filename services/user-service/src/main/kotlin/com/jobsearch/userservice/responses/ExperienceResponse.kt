package com.jobsearch.userservice.responses

import java.sql.Timestamp
import java.util.*

data class ExperienceResponse(
    val experienceId: UUID,
    val companyName: String,
    val role: String,
    val startDate: Timestamp,
    val endDate: Timestamp,
)
