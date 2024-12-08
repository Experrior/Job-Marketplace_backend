package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.VerificationResult

interface VerificationService {
    fun sendVerificationEmail(user: User)
    fun verifyUserByToken(token: String): User
    fun sendVerificationEmail(company: Company)
    fun verifyCompanyByToken(token: String): Company
    fun verifyByToken(token: String): VerificationResult
    fun sendEmployeeVerificationEmail(recruiter: User, companyEmail: String)
    fun approveEmployee(token: String)
    fun rejectEmployee(token: String)
}