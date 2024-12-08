package com.jobsearch.jobservice.replica_repositories

import com.jobsearch.jobservice.entities.quizzes.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuizRepositoryReplica: JpaRepository<Quiz, UUID> {
    fun findByS3QuizPath(s3QuizPath: String): Quiz?
}