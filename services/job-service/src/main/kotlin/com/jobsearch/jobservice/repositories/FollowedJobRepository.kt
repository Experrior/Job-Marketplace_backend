package com.jobsearch.jobservice.repositories

import com.jobsearch.jobservice.entities.FollowedJobs
import com.jobsearch.jobservice.entities.UserJobId
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FollowedJobRepository: JpaRepository<FollowedJobs, UserJobId>{
    fun existsByUserIdAndJobId(userId: UUID, jobId: UUID): Boolean
    fun deleteByUserIdAndJobId(userId: UUID, jobId: UUID)
    fun findByUserId(userId: UUID): List<FollowedJobs>
}