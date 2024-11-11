package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Education
import com.jobsearch.userservice.requests.EducationRequest
import java.util.*

interface EducationService {
    fun getEducationByUserProfile(userId: UUID): List<Education>
    fun createEducation(userId: UUID, educationRequest: EducationRequest): Education
    fun updateEducation(userId: UUID, educationId: UUID, educationRequest: EducationRequest): Education
    fun getEducationById(userId: UUID, educationId: UUID): Education
    fun deleteEducationById(userId: UUID, educationId: UUID): Boolean
    fun deleteAllUserEducations(userId: UUID): Boolean
}