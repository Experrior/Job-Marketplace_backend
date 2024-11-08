package com.jobsearch.jobservice.requests

import com.jobsearch.jobservice.entities.Skill

data class JobRequest(
    val title: String,
    val description: String,
    val location: String,
    val salary: Int?,
    val requiredSkills: List<Skill>,
    val requiredExperience: String
)
