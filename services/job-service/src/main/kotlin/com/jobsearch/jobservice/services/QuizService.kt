package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.responses.QuizResponse
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface QuizService {
    fun createQuiz(recruiterId: UUID, quizConfig: MultipartFile): QuizResponse
    fun recruiterQuizzes(recruiterId: UUID): List<QuizResponse>
}