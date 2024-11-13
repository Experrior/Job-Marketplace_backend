package com.jobsearch.jobservice.responses

import com.jobsearch.jobservice.entities.Skill
import java.sql.Timestamp
import java.util.*

data class JobResponse(
    val jobId: UUID,
    val recruiterId: UUID,
    val companyId: UUID,
    val title: String,
    val description: String,
    val requiredSkills: List<Skill>,
    val requiredExperience: String,
    val location: String,
    val salary: Int?,
    val createdAt: Timestamp,
    val updatedAt: Timestamp?,
    val isDeleted: Boolean,
    val quizId: UUID?
)
