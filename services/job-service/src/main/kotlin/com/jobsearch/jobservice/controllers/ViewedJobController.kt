package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.responses.ViewedJobResponse
import com.jobsearch.jobservice.services.ViewedJobService
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class ViewedJobController(
    private val viewedJobService: ViewedJobService
) {
    @QueryMapping
    fun viewedJobs(@AuthenticationPrincipal userId: UUID): List<ViewedJobResponse> {
        return viewedJobService.getViewedJobs(userId)
    }
}