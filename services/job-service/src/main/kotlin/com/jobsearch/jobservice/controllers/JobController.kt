package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.services.JobService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class JobController(
    private val jobService: JobService
) {

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun createJob(@Argument jobRequest: JobRequest): ResponseEntity<String> {
        jobService.createJob(jobRequest)
        return ResponseEntity.ok("Job created")
    }

    @QueryMapping
    fun jobById(@Argument jobId: UUID): Job{
        return jobService.getJobById(jobId)
    }

    @QueryMapping
    fun allJobs(@Argument limit: Int,
                @Argument offset: Int): List<Job>{
        return jobService.getAllJobs(limit, offset)
    }
}