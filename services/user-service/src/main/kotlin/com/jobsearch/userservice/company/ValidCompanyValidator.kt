package com.jobsearch.userservice.company

import com.jobsearch.userservice.entities.Company
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidCompanyValidator : ConstraintValidator<ValidCompany, String?> {

    override fun isValid(company: String?, context: ConstraintValidatorContext): Boolean {
        return company?.let { name ->
            Company.entries.any { it.name.equals(name, ignoreCase = true) }
        } ?: true
    }
}