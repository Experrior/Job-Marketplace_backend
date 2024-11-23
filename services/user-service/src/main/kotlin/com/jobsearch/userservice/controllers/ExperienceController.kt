package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.requests.ExperienceRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.ExperienceResponse
import com.jobsearch.userservice.services.ExperienceService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class ExperienceController(
    private val experienceService: ExperienceService
) {
    @MutationMapping
    fun addExperience(
        @AuthenticationPrincipal userId: UUID,
        @Argument @Valid experienceRequest: ExperienceRequest
    ): List<ExperienceResponse> {
        return experienceService.addExperience(userId, experienceRequest)
    }

    @MutationMapping
    fun deleteExperienceById(
        @AuthenticationPrincipal userId: UUID,
        @Argument experienceId: String
    ): List<ExperienceResponse> {
        return experienceService.deleteExperienceById(userId, UUID.fromString(experienceId))
    }

    @MutationMapping
    fun deleteAllUserExperiences(
        @AuthenticationPrincipal userId: UUID
    ): DeleteResponse {
        return experienceService.deleteAllUserExperiences(userId)
    }
}