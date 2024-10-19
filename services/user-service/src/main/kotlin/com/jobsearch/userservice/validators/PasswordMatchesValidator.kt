package com.jobsearch.userservice.validators

import com.jobsearch.userservice.requests.UpdatePasswordRequest
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordMatchesValidator : ConstraintValidator<PasswordMatches, UpdatePasswordRequest> {
    override fun isValid(
        value: UpdatePasswordRequest,
        context: ConstraintValidatorContext
    ): Boolean {
        return value.password == value.confirmPassword
    }
}