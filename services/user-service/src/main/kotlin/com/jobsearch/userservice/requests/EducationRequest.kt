package com.jobsearch.userservice.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.YearMonth

data class EducationRequest(
    @field:NotBlank(message = "Institution name must not be blank")
    val institutionName: String,

    @field:NotBlank(message = "Degree must not be blank")
    val degree: String,

    @field:NotNull(message = "Start date must not be null")
    @field:PastOrPresent(message = "Start date must be in the past or present")
    val startDate: YearMonth,

    @field:NotNull(message = "End date must not be null")
    val endDate: YearMonth
)