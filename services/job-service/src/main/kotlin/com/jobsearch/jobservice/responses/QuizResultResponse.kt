package com.jobsearch.jobservice.responses

import java.util.*

data class QuizResultResponse (
    val quizResultId: UUID,
    val quizId: UUID,
    val applicantId: UUID,
    val score: Int,
    val timeTaken: Int?
)