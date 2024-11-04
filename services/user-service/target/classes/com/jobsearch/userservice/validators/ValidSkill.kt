package com.jobsearch.userservice.validators

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidSkillValidator::class])
annotation class ValidSkill(
    val message: String = "Invalid skill",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)