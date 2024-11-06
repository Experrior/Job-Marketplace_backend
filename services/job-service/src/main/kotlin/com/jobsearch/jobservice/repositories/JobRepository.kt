package com.jobsearch.jobservice.repositories

import com.jobsearch.jobservice.entities.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JobRepository: JpaRepository<Job, UUID> {
    fun findJobByJobIdAndIsDeletedFalse(jobId: UUID): Job?
    fun findJobsByRecruiterId(recruiterId: UUID): List<Job>
    fun findJobsByCompanyIdAndIsDeletedFalse(companyId: UUID): List<Job>
    fun findAllByIsDeletedFalse(pageable: Pageable): Page<Job>
    fun findJobByJobId(jobId: UUID): Job?
}