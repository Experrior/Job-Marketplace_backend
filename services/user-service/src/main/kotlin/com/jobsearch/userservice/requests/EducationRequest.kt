package com.jobsearch.userservice.requests

import java.time.YearMonth

data class EducationRequest(
    val institutionName: String,
    val degree: String,
    val startDate: YearMonth,
    val endDate: YearMonth
)