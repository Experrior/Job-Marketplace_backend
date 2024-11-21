package com.jobsearch.userservice.requests

import java.time.YearMonth

data class ExperienceRequest(
    val companyName: String,
    val role: String,
    val startDate: YearMonth,
    val endDate: YearMonth,
)