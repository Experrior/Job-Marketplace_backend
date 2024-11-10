package com.jobsearch.jobservice.entities.quizzes

data class QuestionConfig(
    val questionId: Int,
    val questionText: String,
    val type: QuestionType,
    val answers: List<Answer>
)