package com.jobsearch.jobservice.repositories

import com.jobsearch.jobservice.entities.QuizResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface QuizResultRepository: JpaRepository<QuizResult, UUID> {
}