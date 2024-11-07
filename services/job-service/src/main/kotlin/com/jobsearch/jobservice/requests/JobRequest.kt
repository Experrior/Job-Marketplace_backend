package com.jobsearch.jobservice.requests

data class JobRequest(
    val title: String,
    val description: String,
    val location: String,
    val salary: Int?,
    val requiredSkills: String,
    val requiredExperience: String
)
