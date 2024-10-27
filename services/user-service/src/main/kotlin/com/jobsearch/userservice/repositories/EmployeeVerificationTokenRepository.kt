package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.EmployeeVerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EmployeeVerificationTokenRepository: JpaRepository<EmployeeVerificationToken, UUID> {
    fun findByToken(token: String): EmployeeVerificationToken?
}