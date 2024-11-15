package com.jobsearch.userservice.services.auth.tokens

import com.jobsearch.userservice.responses.TokenResponse

interface RefreshTokenService {
    fun refreshAccessToken(refreshToken: String): TokenResponse
}