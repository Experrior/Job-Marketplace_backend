package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.ResetPasswordToken
import com.jobsearch.userservice.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ResetPasswordTokenRepository: JpaRepository<ResetPasswordToken, UUID> {
    fun findByToken(token: String): ResetPasswordToken?
    fun deleteByToken(token: String)
    fun findByUser(user: User): ResetPasswordToken?
}