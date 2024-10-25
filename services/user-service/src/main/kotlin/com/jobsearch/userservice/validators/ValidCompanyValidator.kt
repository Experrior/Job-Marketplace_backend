package com.jobsearch.userservice.validators

import com.jobsearch.userservice.entities.CompanyEnum
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidCompanyValidator : ConstraintValidator<ValidCompany, String?> {

    override fun isValid(company: String?, context: ConstraintValidatorContext): Boolean {
        return company?.let { name ->
            CompanyEnum.entries.any { it.name.equals(name, ignoreCase = true) }
        } ?: true
    }
}