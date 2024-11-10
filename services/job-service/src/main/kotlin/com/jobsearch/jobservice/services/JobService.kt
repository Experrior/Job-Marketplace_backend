package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobFilterRequest
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface JobService {
    fun createJob(jobRequest: JobRequest): Job
    fun deleteJobById(jobId: UUID): DeleteJobResponse
    fun updateJobById(jobId: UUID, jobRequest: JobRequest): Job
    fun getJobsByRecruiter(recruiterId: UUID): List<Job>
    fun getJobById(jobId: UUID): Job
    fun getJobsByCompany(companyId: UUID): List<Job>
    fun getJobByIdAndDeleteFalse(jobId: UUID): Job
    fun getFilteredJobs(filter: JobFilterRequest?, pageable: Pageable): Page<Job>
    fun restoreJobById(jobId: UUID): Job
}