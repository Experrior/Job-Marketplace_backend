package com.jobsearch.jobservice.replica_repositories

import com.jobsearch.jobservice.entities.QuizResult
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface QuizResultRepositoryReplica: JpaRepository<QuizResult, UUID> {
}