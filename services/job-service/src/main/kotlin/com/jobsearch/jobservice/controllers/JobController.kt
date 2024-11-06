package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import com.jobsearch.jobservice.services.JobService
import org.springframework.data.domain.Page
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
        @Argument jobId: UUID
    ): DeleteJobResponse {
        return jobService.deleteJobById(jobId)

    }

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun updateJob(
        @Argument jobId: UUID,
        @Argument jobRequest: JobRequest
    ): Job {
        return try {
            jobService.updateJobById(jobId, jobRequest)
        } catch (e: IllegalArgumentException) {
            Job()
        }
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun restoreJob(
        @Argument jobId: UUID
    ): Job {
        return jobService.restoreJobById(jobId)
    }

    @QueryMapping
    fun allJobs(
        @Argument limit: Int?,
        @Argument offset: Int?
    ): Page<Job> {
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
        @Argument companyId: UUID
    ): List<Job> {
        return jobService.getJobsByCompany(companyId)
    }

    @QueryMapping
    fun jobById(
        @Argument jobId: UUID
    ): Job {
        return jobService.getJobByIdAndDeleteFalse(jobId)
    }

}