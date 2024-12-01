package com.jobsearch.userservice.responses

import com.jobsearch.userservice.entities.User
import java.sql.Timestamp
import java.util.*

data class UserProfileResponse(
    val profileId: UUID,
    val user: User,
    val resumes: List<ResumeResponse>,
    val skills: List<SkillResponse>,
    val experiences: List<ExperienceResponse>,
    val educations: List<EducationResponse>,
    val links: List<LinkResponse>,
    val profilePictureUrl: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp?
)
