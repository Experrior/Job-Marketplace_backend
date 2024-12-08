package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.requests.JobFilterRequest
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import com.jobsearch.jobservice.responses.FollowJobResponse
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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
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
        @AuthenticationPrincipal recruiterId: UUID
    ): List<JobResponse> {
        return jobService.getJobsByRecruiter(recruiterId)
    }

    @QueryMapping
    fun jobsByCompany(
        @Argument companyId: UUID
    ): List<JobResponse> {
        return jobService.getJobsByCompany(companyId)
    }

    @QueryMapping
    fun jobById(
        @AuthenticationPrincipal userId: UUID?,
        @Argument jobId: UUID
    ): JobResponse {
        return jobService.getJobByIdAndDeleteFalse(userId, jobId)
    }


    @GetMapping("/getJob")
    fun jobByIdRest(
        @Argument jobId: UUID
    ): JobResponse {
        return jobService.getJobByIdAndDeleteFalse(null, jobId)
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

    @PostMapping("/getJobs")
    fun jobsRest(
        @Argument limit: Int?,
        @Argument offset: Int?,
        @RequestBody filter: JobFilterRequest?
    ): Page<JobResponse> {
        val pageable = PageRequest.of(offset ?: 0, limit ?: 10)
        return jobService.getFilteredJobs(filter, pageable)
    }

    @MutationMapping
    fun toggleFollowJob(
        @Argument jobId: UUID,
        @AuthenticationPrincipal userId: UUID
    ): FollowJobResponse {
        return jobService.toggleFollowJob(jobId, userId)
    }

    @QueryMapping
    fun followedJobs(
        @AuthenticationPrincipal userId: UUID
    ): List<JobResponse> {
        return jobService.getFollowedFilteredJobs(userId)
    }
}