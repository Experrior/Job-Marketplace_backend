package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.services.JobService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/jobs")
class JobController(
    private val jobService: JobService
) {
    @PostMapping("/create")
    fun createJob(
        @RequestBody jobRequest: JobRequest
    ): ResponseEntity<Job> {
        return ResponseEntity(
            jobService.createJob(jobRequest),
            HttpStatus.CREATED
        )
    }

    @DeleteMapping("/{jobId}")
    fun deleteJob(
        @PathVariable jobId: String
    ): ResponseEntity<String> {
        return try {
            jobService.deleteJobById(UUID.fromString(jobId))
            ResponseEntity("Job deleted", HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body("Invalid job ID")
        }
    }

    @PutMapping("/{jobId}")
    fun updateJob(
        @PathVariable jobId: String,
        @RequestBody jobRequest: JobRequest
    ): ResponseEntity<Job> {
        return try {
            ResponseEntity(
                jobService.updateJobById(UUID.fromString(jobId), jobRequest),
                HttpStatus.OK
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(Job())
        }
    }

    @GetMapping
    fun getJobs(): ResponseEntity<List<Job>> {
        return ResponseEntity.ok(jobService.getJobs())
    }

    @GetMapping("/recruiter")
    fun getJobsByRecruiter(
        @AuthenticationPrincipal recruiterId: String
    ): ResponseEntity<List<Job>> {
        return try {
            ResponseEntity.ok(jobService.getJobsByRecruiter(UUID.fromString(recruiterId)))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(emptyList())
        }
    }

    @GetMapping("/company/{companyId}")
    fun getJobsByCompany(
        @PathVariable companyId: String
    ): ResponseEntity<List<Job>> {
        return try {
            ResponseEntity.ok(jobService.getJobsByCompany(UUID.fromString(companyId)))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(emptyList())
        }
    }

    @GetMapping("/{jobId}")
    fun getJobById(
        @PathVariable jobId: String
    ): ResponseEntity<Job> {
        return try {
            ResponseEntity.ok(jobService.getJobById(UUID.fromString(jobId)))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(Job())
        }
    }

}