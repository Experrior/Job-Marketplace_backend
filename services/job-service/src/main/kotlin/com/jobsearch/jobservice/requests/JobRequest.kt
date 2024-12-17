package com.jobsearch.jobservice.requests

import com.jobsearch.jobservice.entities.Skill
import java.util.*

data class JobRequest(
    val title: String,
    val category: String,
    val description: String,
    val location: String,
    val salary: Int?,
    val requiredSkills: List<Skill>,
    val requiredExperience: Int?,
    val employmentType: String?,
    val workLocation: String?,
    val experienceLevel: String?,
    val quizId: UUID?
)
