package com.jobsearch.userservice.requests

import com.jobsearch.userservice.validators.PasswordMatches
import com.jobsearch.userservice.validators.ValidCompany
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@PasswordMatches
data class RegistrationRequest(
    @field:NotBlank(message = "Email is mandatory")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:ValidCompany(message = "Company must be valid")
    val company: String? = null,

    @field:NotBlank(message = "First name is mandatory")
    @field:Pattern(regexp = "^[a-zA-Z'\\-\\s]+$", message = "First name contains invalid characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is mandatory")
    @field:Pattern(regexp = "^[a-zA-Z'\\-\\s]+$", message = "Last name contains invalid characters")
    val lastName: String,

    @field:NotBlank(message = "Password is mandatory")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(regexp = ".*[A-Z].*", message = "Password must have at least one uppercase letter")
    @field:Pattern(regexp = ".*\\d.*", message = "Password must have at least one digit")
    override val password: String,

    override val confirmPassword: String
): PasswordConfirmation