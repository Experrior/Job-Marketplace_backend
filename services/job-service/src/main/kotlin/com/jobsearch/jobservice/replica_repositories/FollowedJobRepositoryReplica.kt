package com.jobsearch.jobservice.replica_repositories

import com.jobsearch.jobservice.entities.FollowedJobs
import com.jobsearch.jobservice.entities.UserJobId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
@Repository
interface FollowedJobRepositoryReplica: JpaRepository<FollowedJobs, UserJobId>{
    fun existsByUserIdAndJobId(userId: UUID, jobId: UUID): Boolean
    fun deleteByUserIdAndJobId(userId: UUID, jobId: UUID)
    fun findByUserId(userId: UUID): List<FollowedJobs>
}