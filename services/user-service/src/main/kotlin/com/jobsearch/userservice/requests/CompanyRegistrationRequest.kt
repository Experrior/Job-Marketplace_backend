package com.jobsearch.userservice.requests

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CompanyRegistrationRequest(
    @JsonProperty("email")
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @JsonProperty("companyName")
    @field:NotBlank(message = "Company name is required")
    @field:Size(max = 100, message = "Company name must not exceed 100 characters")
    val companyName: String,

    @JsonProperty("industry")
    @field:NotBlank(message = "Industry is required")
    val industry: String,

    @JsonProperty("description")
    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String?
)