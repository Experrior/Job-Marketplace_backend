package com.jobsearch.userservice.validators

import com.jobsearch.userservice.entities.ProficiencyLevel
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidProfLevelValidator : ConstraintValidator<ValidProficiencyLevel, String?> {

    override fun isValid(proficiencyLevel: String?, context: ConstraintValidatorContext): Boolean {
        return proficiencyLevel?.let { name ->
            ProficiencyLevel.entries.any { it.name.equals(name, ignoreCase = true) }
        } ?: true
    }
}