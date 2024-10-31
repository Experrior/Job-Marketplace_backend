package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.requests.JobRequest
import java.util.*

interface JobService {
    fun createJob(jobRequest: JobRequest): Job
    fun getJobById(jobId: UUID): Job
    fun getAllJobs(limit: Int = 10, offset: Int = 0): List<Job>
}