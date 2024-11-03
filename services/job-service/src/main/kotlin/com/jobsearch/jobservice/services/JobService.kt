package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobRequest
import java.util.*

interface JobService {
    fun createJob(jobRequest: JobRequest): Job
    fun deleteJobById(jobId: UUID)
    fun updateJobById(jobId: UUID, jobRequest: JobRequest): Job
    fun getJobsByRecruiter(recruiterId: UUID): List<Job>
    fun getJobs(): List<Job>
    fun getJobById(jobId: UUID): Job
    fun getJobsByCompany(companyId: UUID): List<Job>
}