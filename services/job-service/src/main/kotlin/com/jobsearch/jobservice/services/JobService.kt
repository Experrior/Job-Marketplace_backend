package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobFilterRequest
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import com.jobsearch.jobservice.responses.FollowJobResponse
import com.jobsearch.jobservice.responses.JobResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface JobService {
    fun createJob(jobRequest: JobRequest): JobResponse
    fun deleteJobById(jobId: UUID): DeleteJobResponse
    fun updateJobById(jobId: UUID, jobRequest: JobRequest): JobResponse
    fun getJobsByRecruiter(recruiterId: UUID): List<JobResponse>
    fun getJobEntityById(jobId: UUID): Job
    fun getJobById(jobId: UUID): JobResponse
    fun getJobsByCompany(companyId: UUID): List<JobResponse>
    fun getJobByIdAndDeleteFalse(userId: UUID?, jobId: UUID): JobResponse
    fun getFilteredJobs(filter: JobFilterRequest?, pageable: Pageable): Page<JobResponse>
    fun restoreJobById(jobId: UUID): JobResponse
    fun toggleFollowJob(jobId: UUID, userId: UUID): FollowJobResponse
    fun getFollowedFilteredJobs(userId: UUID): List<JobResponse>
}