package com.jobsearch.userservice.validators

import com.jobsearch.userservice.entities.SkillType
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidSkillValidator : ConstraintValidator<ValidSkill, String?> {

    override fun isValid(skill: String?, context: ConstraintValidatorContext): Boolean {
        return skill?.let { name ->
            SkillType.entries.any { it.name.equals(name, ignoreCase = true) }
        } ?: true
    }
}