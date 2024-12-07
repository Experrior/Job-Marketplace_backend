package com.jobsearch.jobservice.replica_repositories

import com.jobsearch.jobservice.entities.quizzes.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
@Repository
interface QuizRepositoryReplica: JpaRepository<Quiz, UUID> {
    fun findByS3QuizPath(s3QuizPath: String): Quiz?
}