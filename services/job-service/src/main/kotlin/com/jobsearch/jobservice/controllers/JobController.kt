package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import com.jobsearch.jobservice.services.JobService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class JobController(
    private val jobService: JobService
) {
    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun createJob(
        @Argument jobRequest: JobRequest
    ): Job {
        return jobService.createJob(jobRequest)
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun deleteJob(
        @Argument jobId: String
    ): DeleteJobResponse {
        return try {
            jobService.deleteJobById(UUID.fromString(jobId))
            DeleteJobResponse(success = true, message = "Job deleted")
        } catch (e: IllegalArgumentException) {
            DeleteJobResponse(success = false, message = "Invalid job ID")
        }
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun updateJob(
        @Argument jobId: String,
        @Argument jobRequest: JobRequest
    ): Job {
        return try {
            jobService.updateJobById(UUID.fromString(jobId), jobRequest)
        } catch (e: IllegalArgumentException) {
            Job()
        }
    }

    @QueryMapping
    fun allJobs(
        @Argument limit: Int,
        @Argument offset: Int
    ): List<Job> {
        return jobService.getJobs(limit, offset)
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @QueryMapping
    fun jobsByRecruiter(
        @AuthenticationPrincipal recruiterId: String
    ): List<Job> {
        return try {
            jobService.getJobsByRecruiter(UUID.fromString(recruiterId))
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }

    @QueryMapping
    fun jobsByCompany(
        @Argument companyId: String
    ): List<Job> {
        return try {
            jobService.getJobsByCompany(UUID.fromString(companyId))
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }

    @QueryMapping
    fun jobById(
        @Argument jobId: String
    ): Job {
        return try {
            jobService.getJobById(UUID.fromString(jobId))
        } catch (e: IllegalArgumentException) {
            Job()
        }
    }

}