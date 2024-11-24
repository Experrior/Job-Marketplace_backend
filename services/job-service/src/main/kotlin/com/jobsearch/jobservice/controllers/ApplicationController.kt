package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.responses.ApplyForJobResponse
import com.jobsearch.jobservice.responses.SetApplicationStatusResponse
import com.jobsearch.jobservice.services.JobApplicationService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/applications")
class ApplicationController(
    private val jobApplicationService: JobApplicationService
) {
    @PreAuthorize("hasRole('APPLICANT')")
    @MutationMapping
    fun applyForJob(
        @AuthenticationPrincipal userId: UUID,
        @Argument jobId: UUID,
        @Argument resumeId: UUID,
        @Argument quizResultId: UUID?
    ): Application {
        return jobApplicationService.applyForJob(jobId, userId, resumeId, quizResultId)
    }

    @PreAuthorize("hasRole('APPLICANT')")
    @QueryMapping
    fun userApplications(
        @AuthenticationPrincipal userId: UUID
    ): List<Application> {
        return jobApplicationService.getUserApplications(userId)
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @QueryMapping
    fun jobApplications(
        @Argument jobId: UUID,
        @Argument sortOrder: String?
    ): List<Application> {
        val applications = jobApplicationService.getJobApplications(jobId)
        return when (sortOrder?.lowercase(Locale.getDefault())) {
            "asc" -> applications.sortedBy { it.quizResult?.score }
            "desc" -> applications.sortedByDescending { it.quizResult?.score }
            else -> applications
        }
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun setApplicationStatus(
        @Argument applicationId: UUID,
        @Argument status: String
    ): SetApplicationStatusResponse {
        return jobApplicationService.setApplicationStatus(
                applicationId,
                ApplicationStatus.valueOf(status))
    }
}