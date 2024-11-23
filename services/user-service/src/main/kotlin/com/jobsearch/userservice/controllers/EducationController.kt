package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.exceptions.InvalidUUIDException
import com.jobsearch.userservice.requests.EducationRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.EducationResponse
import com.jobsearch.userservice.services.EducationService
import jakarta.validation.Valid
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
    fun currentUserEducation(@AuthenticationPrincipal userId: UUID): List<EducationResponse> {
        return educationService.getEducationsByUserProfile(userId)
    }

    @QueryMapping
    fun educationById(@AuthenticationPrincipal userId: UUID,
                      @Argument educationId: String): EducationResponse {
        return educationService.getEducationById(
            userId,
            getUUIDFromString(educationId))
    }

    @MutationMapping
    fun addEducation(@AuthenticationPrincipal userId: UUID,
                        @Argument @Valid educationRequest: EducationRequest): List<EducationResponse> {
        return educationService.addEducation(userId, educationRequest)
    }

    @MutationMapping
    fun updateEducation(@AuthenticationPrincipal userId: UUID,
                        @Argument educationId: String,
                        @Argument @Valid educationRequest: EducationRequest): List<EducationResponse> {
        return educationService.updateEducation(
            userId,
            getUUIDFromString(educationId),
            educationRequest)
    }

    @MutationMapping
    fun deleteEducationById(@AuthenticationPrincipal userId: UUID,
                            @Argument educationId: String): List<EducationResponse> {
        return educationService.deleteEducationById(
            userId,
            getUUIDFromString(educationId))
    }

    @MutationMapping
    fun deleteAllUserEducations(@AuthenticationPrincipal userId: UUID): DeleteResponse {
        return educationService.deleteAllUserEducations(userId)
    }

    private fun getUUIDFromString(educationId: String): UUID {
        return try {
            UUID.fromString(educationId)
        } catch (e: IllegalArgumentException) {
            throw InvalidUUIDException("Invalid UUID format for educationId: $educationId")
        }
    }
}