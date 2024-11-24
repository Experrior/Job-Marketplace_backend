package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.responses.ApplyForJobResponse
import com.jobsearch.jobservice.responses.SetApplicationStatusResponse
import com.jobsearch.jobservice.services.JobApplicationService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Controller
@RequestMapping("/applications")
class ApplicationController(
    private val jobApplicationService: JobApplicationService
) {
    @PostMapping("/{jobId}/apply", consumes = ["multipart/form-data"])
    fun applyForJob(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable jobId: UUID,
        @RequestParam("resume", required = true) resume: MultipartFile,
        @RequestParam("quizResultId", required = false) quizResultId: UUID?
    ): ResponseEntity<ApplyForJobResponse> {
        return ResponseEntity(
                jobApplicationService.applyForJob(jobId, userId, resume, quizResultId),
                HttpStatus.CREATED
            )
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