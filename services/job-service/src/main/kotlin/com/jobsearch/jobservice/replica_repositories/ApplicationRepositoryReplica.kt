package com.jobsearch.jobservice.replica_repositories

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.Job
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface ApplicationRepositoryReplica: JpaRepository<Application, UUID> {
    fun findApplicationsByUserId(userId: UUID): List<Application>
    fun findApplicationByApplicationId(applicationId: UUID): Application?
    fun findApplicationsByJob(job: Job): List<Application>
    fun findApplicationByJobAndUserId(job: Job, userId: UUID): Application?
}