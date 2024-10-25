package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.Company
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CompanyRepository: JpaRepository<Company, UUID> {
    fun existsByEmail(email: String): Boolean
    fun existsByName(name: String): Boolean
}