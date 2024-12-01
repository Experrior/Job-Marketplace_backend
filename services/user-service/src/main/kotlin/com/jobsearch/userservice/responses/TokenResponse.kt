package com.jobsearch.userservice.responses

import java.util.*

data class TokenResponse(
    val accessToken: String,
    val expiresIn: Long,
    val refreshToken: String,
    val refreshExpiresIn: Long,
    val userId: UUID?,
    val role: String,
    val firstName: String,
    val lastName: String
)
