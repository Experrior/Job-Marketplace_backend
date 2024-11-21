package com.jobsearch.jobservice.requests

import java.util.*

data class QuizResultRequest (
    val quizId: UUID,
    val score: Double,
    val timeTaken: Int? = null
)