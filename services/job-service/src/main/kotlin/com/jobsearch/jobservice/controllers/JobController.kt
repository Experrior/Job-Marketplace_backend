package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.services.JobService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/job")
class JobController(
    private val jobService: JobService
) {
    @PostMapping("/create")
    fun createJob(
        @RequestBody jobRequest: JobRequest
    ): ResponseEntity<String> {
        jobService.createJob(jobRequest)
        return ResponseEntity.ok("Job created")
    }
}