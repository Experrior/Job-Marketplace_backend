package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import java.util.*

interface JobApplicationService {
    fun applyForJob(jobId: UUID, userId: UUID): Application
    fun getUserApplications(userId: UUID): List<Application>
    fun getJobApplications(jobId: UUID): List<Application>
    fun setApplicationStatus(applicationId: UUID, status: ApplicationStatus)
}