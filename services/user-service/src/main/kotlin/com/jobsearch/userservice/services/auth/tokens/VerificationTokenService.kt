package com.jobsearch.userservice.services.auth.tokens

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.entities.EmployeeVerificationToken
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.VerificationToken

interface VerificationTokenService {
    fun generateVerificationToken(user: User): VerificationToken
    fun generateVerificationToken(company: Company): VerificationToken
    fun getVerificationToken(token: String): VerificationToken
    fun deleteVerificationToken(token: VerificationToken)

    fun generateEmployeeVerificationToken(user: User): EmployeeVerificationToken
    fun getEmployeeVerificationToken(token: String): EmployeeVerificationToken
    fun deleteEmployeeVerificationToken(token: EmployeeVerificationToken)
}