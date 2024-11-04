package com.jobsearch.userservice.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Email is mandatory")
    @field:Email(message = "Email must be valid")
    val email: String,
    @field:NotBlank(message = "Password is mandatory")
    val password: String
)
