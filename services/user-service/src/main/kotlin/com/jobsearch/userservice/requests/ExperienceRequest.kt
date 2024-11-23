package com.jobsearch.userservice.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.YearMonth

data class ExperienceRequest(
    @field:NotBlank(message = "Company name must not be blank")
    val companyName: String,

    @field:NotBlank(message = "Role must not be blank")
    val role: String,

    @field:NotNull(message = "Start date must not be null")
    @field:PastOrPresent(message = "Start date must be in the past or present")
    val startDate: YearMonth,

    @field:NotNull(message = "End date must not be null")
    @field:PastOrPresent(message = "End date must be in the past or present")
    val endDate: YearMonth,
)