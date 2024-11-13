package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.requests.JobFilterRequest
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import com.jobsearch.jobservice.responses.JobResponse
import com.jobsearch.jobservice.services.JobService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
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
    ): JobResponse {
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
    ): JobResponse {
        return jobService.updateJobById(jobId, jobRequest)
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun restoreJob(
        @Argument jobId: UUID
    ): JobResponse {
        return jobService.restoreJobById(jobId)
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @QueryMapping
    fun jobsByRecruiter(
        @AuthenticationPrincipal recruiterId: String
    ): List<JobResponse> {
        return try {
            jobService.getJobsByRecruiter(UUID.fromString(recruiterId))
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }

    @QueryMapping
    fun jobsByCompany(
        @Argument companyId: UUID
    ): List<JobResponse> {
        return jobService.getJobsByCompany(companyId)
    }

    @QueryMapping
    fun jobById(
        @Argument jobId: UUID
    ): JobResponse {
        return jobService.getJobByIdAndDeleteFalse(jobId)
    }

    @QueryMapping
    fun jobs(
        @Argument filter: JobFilterRequest?,
        @Argument limit: Int?,
        @Argument offset: Int?
    ): Page<JobResponse> {
        val pageable = PageRequest.of(offset ?: 0, limit ?: 10)
        return jobService.getFilteredJobs(filter, pageable)
    }
}