package com.jobsearch.userservice.validators

import com.jobsearch.userservice.services.CompanyService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidCompanyValidator(
    private val companyService: CompanyService
) : ConstraintValidator<ValidCompany, String?> {

    override fun isValid(company: String?, context: ConstraintValidatorContext): Boolean {
        return company?.let { name ->
            companyService.existsByName(name)
        } ?: true
    }
}