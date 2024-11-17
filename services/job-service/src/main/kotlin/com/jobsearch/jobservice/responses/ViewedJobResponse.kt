package com.jobsearch.jobservice.responses

import java.util.*

data class ViewedJobResponse(
    val jobId: UUID,
    val viewCount: Int
)
