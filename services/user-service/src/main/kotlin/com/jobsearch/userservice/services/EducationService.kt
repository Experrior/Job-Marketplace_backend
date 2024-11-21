package com.jobsearch.userservice.services

import com.jobsearch.userservice.requests.EducationRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.EducationResponse
import java.util.*

interface EducationService {
    fun getEducationsByUserProfile(userId: UUID): List<EducationResponse>
    fun addEducation(userId: UUID, educationRequest: EducationRequest): List<EducationResponse>
    fun updateEducation(userId: UUID, educationId: UUID, educationRequest: EducationRequest): List<EducationResponse>
    fun getEducationById(userId: UUID, educationId: UUID): EducationResponse
    fun deleteEducationById(userId: UUID, educationId: UUID): List<EducationResponse>
    fun deleteAllUserEducations(userId: UUID): DeleteResponse
}