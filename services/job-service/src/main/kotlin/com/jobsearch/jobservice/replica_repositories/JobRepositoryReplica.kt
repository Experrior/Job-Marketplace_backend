package com.jobsearch.jobservice.replica_repositories

import com.jobsearch.jobservice.entities.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JobRepositoryReplica: JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {
    fun findJobByJobIdAndIsDeletedFalse(jobId: UUID): Job?
    fun findJobsByRecruiterId(recruiterId: UUID): List<Job>
    fun findJobsByCompanyIdAndIsDeletedFalse(companyId: UUID): List<Job>
    fun findAllByIsDeletedFalse(pageable: Pageable): Page<Job>
    fun findJobByJobId(jobId: UUID): Job?
}