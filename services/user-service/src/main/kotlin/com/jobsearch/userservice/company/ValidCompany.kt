package com.jobsearch.userservice.company

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidCompanyValidator::class])
annotation class ValidCompany(
    val message: String = "Invalid company",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)