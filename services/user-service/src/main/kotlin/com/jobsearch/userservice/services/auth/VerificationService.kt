package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.User

interface VerificationService {
    fun sendVerificationEmail(user: User)
    fun verifyUserByToken(token: String): User
}