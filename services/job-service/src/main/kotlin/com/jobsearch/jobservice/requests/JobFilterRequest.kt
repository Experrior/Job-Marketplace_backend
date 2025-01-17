package com.jobsearch.jobservice.requests

import java.util.*

data class JobFilterRequest(
    val location: String? = null,
    val requiredExperience: String? = null,
    val requiredSkills: List<String>? = null,
    val workLocation: String? = null,
    val employmentType: String? = null,
    val experienceLevel: String? = null,
    val companyId: UUID? = null,
    val hasSalary: Boolean? = null,
    val minSalary: Int? = null,
    val maxSalary: Int? = null,
)