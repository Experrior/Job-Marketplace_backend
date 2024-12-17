package com.jobsearch.userservice.entities

sealed class VerificationResult {
    data class UserVerified(val user: User) : VerificationResult()
    data class CompanyVerified(val company: Company) : VerificationResult()
}
