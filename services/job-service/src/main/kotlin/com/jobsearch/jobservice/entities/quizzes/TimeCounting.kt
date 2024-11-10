package com.jobsearch.jobservice.entities.quizzes

data class TimeCounting(
    val type: TimeCountingType,
    val timeLimitInSec: Int
)