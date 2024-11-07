package com.jobsearch.jobservice.requests

import java.util.*

data class JobFilterRequest(
    val location: String? = null,
    val requiredExperience: String? = null,
    val companyId: UUID? = null,
    val hasSalary: Boolean? = null,
    val minSalary: Int? = null,
    val maxSalary: Int? = null,
)