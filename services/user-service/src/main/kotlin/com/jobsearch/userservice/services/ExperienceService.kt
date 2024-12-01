package com.jobsearch.userservice.services

import com.jobsearch.userservice.requests.ExperienceRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.ExperienceResponse
import java.util.*

interface ExperienceService {
    fun addExperience(userId: UUID, experienceRequest: ExperienceRequest): ExperienceResponse
    fun deleteExperienceById(userId: UUID, experienceId: UUID): DeleteResponse
    fun deleteAllUserExperiences(userId: UUID): DeleteResponse
}