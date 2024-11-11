package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.VerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface VerificationTokenRepository: JpaRepository<VerificationToken, UUID> {
    fun findByToken(token: String): VerificationToken?
}