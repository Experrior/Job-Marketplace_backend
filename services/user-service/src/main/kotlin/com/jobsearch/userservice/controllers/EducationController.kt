package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.Education
import com.jobsearch.userservice.exceptions.InvalidUUIDException
import com.jobsearch.userservice.requests.EducationRequest
import com.jobsearch.userservice.services.EducationService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class EducationController(
    private val educationService: EducationService
) {
    @QueryMapping
    fun currentUserEducation(@AuthenticationPrincipal userId: String): List<Education> {
        return educationService.getEducationByUserProfile(UUID.fromString(userId))
    }

    @QueryMapping
    fun educationById(@AuthenticationPrincipal userId: String,
                      @Argument educationId: String): Education {
        return educationService.getEducationById(
            UUID.fromString(userId),
            getUUIDFromString(educationId))
    }

    @MutationMapping
    fun createEducation(@AuthenticationPrincipal userId: String,
                        @Argument educationRequest: EducationRequest): Education {
        return educationService.createEducation(UUID.fromString(userId), educationRequest)
    }

    @MutationMapping
    fun updateEducation(@AuthenticationPrincipal userId: String,
                        @Argument educationId: String,
                        @Argument educationRequest: EducationRequest): Education {
        return educationService.updateEducation(
            UUID.fromString(userId),
            getUUIDFromString(educationId),
            educationRequest)
    }

    @MutationMapping
    fun deleteEducationById(@AuthenticationPrincipal userId: String,
                            @Argument educationId: String): Boolean {
        return educationService.deleteEducationById(
            UUID.fromString(userId),
            getUUIDFromString(educationId))
    }

    @MutationMapping
    fun deleteAllUserEducations(@AuthenticationPrincipal userId: String): Boolean {
        return educationService.deleteAllUserEducations(UUID.fromString(userId))
    }

    private fun getUUIDFromString(educationId: String): UUID {
        return try {
            UUID.fromString(educationId)
        } catch (e: IllegalArgumentException) {
            throw InvalidUUIDException("Invalid UUID format for educationId: $educationId")
        }
    }
}