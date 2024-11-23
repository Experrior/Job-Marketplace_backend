package com.jobsearch.userservice.responses

import java.time.YearMonth
import java.util.*

data class ExperienceResponse(
    val experienceId: UUID,
    val companyName: String,
    val role: String,
    val startDate: YearMonth,
    val endDate: YearMonth,
)
