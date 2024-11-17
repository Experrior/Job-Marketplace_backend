package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.UserJobId
import com.jobsearch.jobservice.entities.ViewedJob
import com.jobsearch.jobservice.repositories.ViewedJobRepository
import com.jobsearch.jobservice.responses.ViewedJobResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class ViewedJobServiceImpl(
    private val viewedJobRepository: ViewedJobRepository
): ViewedJobService {
    override fun viewJob(userId: UUID, jobId: UUID) {
        val viewedJob = viewedJobRepository.findById(UserJobId(userId, jobId)).orElse(
            createViewedJob(userId, jobId)
        )

        viewedJob.viewCount += 1
        viewedJobRepository.save(viewedJob)
    }

    override fun getViewedJobs(userId: UUID): List<ViewedJobResponse> {
        return viewedJobRepository.findByUserId(userId).map { createViewedJobResponse(it) }
    }

    private fun createViewedJob(userId: UUID, jobId: UUID): ViewedJob {
        return ViewedJob(userId = userId, jobId = jobId, viewCount = 0)
    }

    private fun createViewedJobResponse(viewedJob: ViewedJob): ViewedJobResponse {
        return ViewedJobResponse(jobId = viewedJob.jobId, viewCount = viewedJob.viewCount)
    }
}