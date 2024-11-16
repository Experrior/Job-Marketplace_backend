package com.jobsearch.jobservice.repositories

import com.jobsearch.jobservice.entities.quizzes.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuizRepository: JpaRepository<Quiz, UUID> {
    fun findByS3QuizPath(s3QuizPath: String): Quiz?
    fun findByQuizId(quizId: UUID): Quiz
}