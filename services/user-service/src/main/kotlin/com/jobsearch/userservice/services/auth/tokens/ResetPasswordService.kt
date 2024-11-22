package com.jobsearch.userservice.services.auth.tokens

import com.jobsearch.userservice.requests.UpdatePasswordRequest

interface ResetPasswordService {
    fun resetPassword(email: String)
    fun updatePassword(token: String, updatePasswordRequest: UpdatePasswordRequest)
    fun validateToken(token: String)
}