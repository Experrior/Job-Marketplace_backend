package com.jobsearch.jobservice.replica_repositories

import com.jobsearch.jobservice.entities.UserJobId
import com.jobsearch.jobservice.entities.ViewedJob
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ViewedJobRepositoryReplica: JpaRepository<ViewedJob, UserJobId> {
    fun findByUserId(userId: UUID): List<ViewedJob>
}