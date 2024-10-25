package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.entities.User

interface VerificationService {
    fun sendVerificationEmail(user: User)
    fun verifyUserByToken(token: String): User
    fun sendVerificationEmail(company: Company)
    fun verifyCompanyByToken(token: String): Company
    fun verifyByToken(token: String)
}