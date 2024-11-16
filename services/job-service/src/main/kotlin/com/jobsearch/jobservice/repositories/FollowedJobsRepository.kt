package com.jobsearch.jobservice.repositories

import com.jobsearch.jobservice.entities.FollowedJobId
import com.jobsearch.jobservice.entities.FollowedJobs
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FollowedJobsRepository: JpaRepository<FollowedJobs, FollowedJobId>{
    fun existsByUserIdAndJobId(userId: UUID, jobId: UUID): Boolean
    fun deleteByUserIdAndJobId(userId: UUID, jobId: UUID)
    fun findByUserId(userId: UUID): List<FollowedJobs>
}