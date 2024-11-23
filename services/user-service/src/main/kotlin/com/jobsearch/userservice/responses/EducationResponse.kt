package com.jobsearch.userservice.responses

import java.time.YearMonth
import java.util.*

data class EducationResponse(
    val educationId: UUID,
    val institutionName: String,
    val degree: String,
    val startDate: YearMonth,
    val endDate: YearMonth,
)
