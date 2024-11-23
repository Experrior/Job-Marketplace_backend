package com.jobsearch.userservice.requests

import com.jobsearch.userservice.validators.PasswordMatches
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@PasswordMatches
data class UpdatePasswordRequest(
    @field:NotBlank(message = "Password is mandatory")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(regexp = ".*[A-Z].*", message = "Password must have at least one uppercase letter")
    @field:Pattern(regexp = ".*\\d.*", message = "Password must have at least one digit")
    override val password: String,

    override val confirmPassword: String
): PasswordConfirmation
