package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.requests.QuizResultRequest
import com.jobsearch.jobservice.responses.QuizResultResponse
import java.util.*

interface QuizResultService {
    fun saveQuizResult(quizResultRequest: QuizResultRequest, userId: UUID): QuizResultResponse
}