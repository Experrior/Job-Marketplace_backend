package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.responses.ApplyForJobResponse
import com.jobsearch.jobservice.responses.SetApplicationStatusResponse
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface JobApplicationService {
    fun applyForJob(jobId: UUID, userId: UUID, resume: MultipartFile, quizResultId: UUID?): ApplyForJobResponse
    fun getUserApplications(userId: UUID): List<Application>
    fun getJobApplications(jobId: UUID): List<Application>
    fun setApplicationStatus(applicationId: UUID, status: ApplicationStatus): SetApplicationStatusResponse
}