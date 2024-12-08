package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.Company
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CompanyRepositoryReplica: JpaRepository<Company, UUID> {
    fun existsByEmail(email: String): Boolean
    fun existsByName(name: String): Boolean
    fun findCompanyByName(name: String): Company
    fun findCompanyByCompanyId(companyId: UUID): Company?
}