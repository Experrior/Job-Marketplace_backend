package com.jobsearch.userservice.validators

import com.jobsearch.userservice.requests.PasswordConfirmation
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordMatchesValidator : ConstraintValidator<PasswordMatches, PasswordConfirmation> {
    override fun isValid(
        value: PasswordConfirmation,
        context: ConstraintValidatorContext
    ): Boolean {
        return value.password == value.confirmPassword
    }
}