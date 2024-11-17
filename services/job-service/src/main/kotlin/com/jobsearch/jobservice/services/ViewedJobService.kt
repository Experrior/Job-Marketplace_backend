package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.responses.ViewedJobResponse
import java.util.*

interface ViewedJobService {
    fun viewJob(userId: UUID, jobId: UUID)
    fun getViewedJobs(userId: UUID): List<ViewedJobResponse>
}