package com.jobsearch.jobservice.entities.quizzes

data class QuizConfig(
    val name: String,
    val timeCounting: TimeCounting,
    val canGoBack: Boolean,
    val questions: List<QuestionConfig>
)