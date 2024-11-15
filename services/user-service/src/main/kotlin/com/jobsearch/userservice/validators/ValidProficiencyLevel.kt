package com.jobsearch.userservice.validators

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidProfLevelValidator::class])
annotation class ValidProficiencyLevel(
    val message: String = "Invalid proficiency level",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)