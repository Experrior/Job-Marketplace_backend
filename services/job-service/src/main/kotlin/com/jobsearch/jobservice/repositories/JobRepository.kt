package com.jobsearch.jobservice.repositories

import com.jobsearch.jobservice.entities.Job
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JobRepository: JpaRepository<Job, UUID> {
    fun findJobByJobId(jobId: UUID): Job?
    fun findJobsByRecruiterId(recruiterId: UUID): List<Job>
    fun findJobsByCompanyId(companyId: UUID): List<Job>
}