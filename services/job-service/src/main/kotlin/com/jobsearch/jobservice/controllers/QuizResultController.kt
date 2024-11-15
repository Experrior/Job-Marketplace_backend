package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.requests.QuizResultRequest
import com.jobsearch.jobservice.responses.QuizResultResponse
import com.jobsearch.jobservice.services.QuizResultService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class QuizResultController(
    private val quizResultService: QuizResultService
) {
    @PreAuthorize("hasRole('APPLICANT')")
    @MutationMapping
    fun submitQuizResult(
        @AuthenticationPrincipal userId: String,
        @Argument quizResultRequest: QuizResultRequest
    ): QuizResultResponse {
        return quizResultService.saveQuizResult(quizResultRequest, UUID.fromString(userId))
    }
}