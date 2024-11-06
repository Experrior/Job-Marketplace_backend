package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.services.JobApplicationService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        @AuthenticationPrincipal userId: String,
        @PathVariable jobId: String,
        @RequestParam("resume", required = true) resume: MultipartFile?
    ): ResponseEntity<Application> {
        return if (resume == null) {
            ResponseEntity.badRequest().body(Application())
        } else {
            try {
                ResponseEntity(
                    jobApplicationService.applyForJob(UUID.fromString(jobId), UUID.fromString(userId), resume),
                    HttpStatus.CREATED
                )
            } catch (e: IllegalArgumentException) {
                ResponseEntity.badRequest().body(Application())
            }
        }
    }

    @QueryMapping
    fun userApplications(
        @AuthenticationPrincipal userId: String
    ): List<Application> {
        return try {
            jobApplicationService.getUserApplications(UUID.fromString(userId))
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }

    @QueryMapping
    fun jobApplications(
        @Argument jobId: String
    ): List<Application> {
        return try {
            jobApplicationService.getJobApplications(UUID.fromString(jobId))
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }

    @MutationMapping
    fun setApplicationStatus(
        @Argument applicationId: String,
        @Argument status: String
    ): String {
        return try {
            jobApplicationService.setApplicationStatus(
                UUID.fromString(applicationId),
                ApplicationStatus.valueOf(status))
            "Application status updated"
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            "Invalid application ID or status"
        }
    }


}