package com.jobsearch.userservice.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CompanyRegistrationRequest(
    @field:Email(message = "Email should be valid")
    @field:NotBlank(message = "Email must not be blank")
    val email: String,

    @field:NotBlank(message = "Company name must not be blank")
    val companyName: String,

    @field:NotBlank(message = "Industry must not be blank")
    val industry: String,

    val description: String?,

    @field:NotBlank(message = "Logo must not be blank")
    val logo: String
)