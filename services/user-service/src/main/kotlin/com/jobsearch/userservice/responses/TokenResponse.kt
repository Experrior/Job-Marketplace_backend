package com.jobsearch.userservice.responses

data class TokenResponse(
    val accessToken: String,
    val expiresIn: Long,
    val refreshToken: String,
    val refreshExpiresIn: Long
)
