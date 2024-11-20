package com.jobsearch.userservice.services

import com.jobsearch.userservice.requests.EducationRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.EducationResponse
import java.util.*

interface EducationService {
    fun getEducationByUserProfile(userId: UUID): List<EducationResponse>
    fun createEducation(userId: UUID, educationRequest: EducationRequest): EducationResponse
    fun updateEducation(userId: UUID, educationId: UUID, educationRequest: EducationRequest): EducationResponse
    fun getEducationById(userId: UUID, educationId: UUID): EducationResponse
    fun deleteEducationById(userId: UUID, educationId: UUID): DeleteResponse
    fun deleteAllUserEducations(userId: UUID): DeleteResponse
}