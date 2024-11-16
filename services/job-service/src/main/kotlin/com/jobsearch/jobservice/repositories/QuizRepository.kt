package com.jobsearch.jobservice.repositories

import com.jobsearch.jobservice.entities.quizzes.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuizRepository: JpaRepository<Quiz, UUID> {
    fun findByS3QuizPath(s3QuizPath: String): Quiz?
}