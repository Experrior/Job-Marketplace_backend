package com.jobsearch.jobservice.repositories

import com.jobsearch.jobservice.entities.UserJobId
import com.jobsearch.jobservice.entities.ViewedJob
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
@Repository
interface ViewedJobRepository: JpaRepository<ViewedJob, UserJobId> {
    fun findByUserId(userId: UUID): List<ViewedJob>
}