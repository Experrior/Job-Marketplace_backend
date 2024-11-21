package com.jobsearch.jobservice.responses

import java.util.*

data class QuizResponse(
    val quizId: UUID,
    val quizName: String,
    val s3QuizUrl: String,
    val createdAt: Date,
    val isDeleted: Boolean
)
