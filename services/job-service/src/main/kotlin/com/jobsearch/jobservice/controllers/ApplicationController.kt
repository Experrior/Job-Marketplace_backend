package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.requests.StatusRequest
import com.jobsearch.jobservice.services.JobApplicationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/applications")
class ApplicationController(
    private val jobApplicationService: JobApplicationService
) {
    @PostMapping("/{jobId}/apply")
    fun applyForJob(
        @AuthenticationPrincipal userId: String,
        @PathVariable jobId: String
    ): ResponseEntity<Application> {
        return try {
            ResponseEntity(
                jobApplicationService.applyForJob(UUID.fromString(jobId), UUID.fromString(userId)),
                HttpStatus.CREATED
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(Application())
        }
    }

    @GetMapping
    fun getUserApplications(
        @AuthenticationPrincipal userId: String
    ): ResponseEntity<List<Application>> {
        return try{
            ResponseEntity.ok(jobApplicationService.getUserApplications(UUID.fromString(userId)))
        } catch (e: IllegalArgumentException){
            ResponseEntity.badRequest().body(emptyList())
        }
    }

    @GetMapping("/{jobId}")
    fun getJobApplications(
        @PathVariable jobId: String
    ): ResponseEntity<List<Application>> {
        return try {
            ResponseEntity.ok(jobApplicationService.getJobApplications(UUID.fromString(jobId)))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(emptyList())
        }
    }

    @PostMapping("/{applicationId}/status")
    fun setApplicationStatus(
        @PathVariable applicationId: String,
        @RequestBody statusRequest: StatusRequest
    ): ResponseEntity<String> {
        return try {
            jobApplicationService.setApplicationStatus(
                UUID.fromString(applicationId),
                ApplicationStatus.valueOf(statusRequest.status))
            ResponseEntity.ok("Application status updated")
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            ResponseEntity.badRequest().body("Invalid application ID or status")
        }
    }


}