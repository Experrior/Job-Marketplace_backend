package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.requests.LoginRequest
import com.jobsearch.userservice.responses.TokenResponse

interface LoginService {
    fun login(loginRequest: LoginRequest): TokenResponse
}