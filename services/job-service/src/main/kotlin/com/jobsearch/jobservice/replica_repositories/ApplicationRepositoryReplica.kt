package com.jobsearch.jobservice.replica_repositories

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.Job
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ApplicationRepositoryReplica: JpaRepository<Application, UUID> {
    fun findApplicationsByUserId(userId: UUID): List<Application>
    fun findApplicationByApplicationId(applicationId: UUID): Application?
    fun findApplicationsByJob(job: Job): List<Application>
    fun findApplicationByJobAndUserId(job: Job, userId: UUID): Application?
}