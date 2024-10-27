package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.entities.User

interface VerificationService {
    fun sendVerificationEmail(user: User)
    fun verifyUserByToken(token: String)
    fun sendVerificationEmail(company: Company)
    fun verifyCompanyByToken(token: String): Company
    fun verifyByToken(token: String)
    fun sendEmployeeVerificationEmail(recruiter: User, companyEmail: String)
    fun approveEmployee(token: String)
    fun rejectEmployee(token: String)
}