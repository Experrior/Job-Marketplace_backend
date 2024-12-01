package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.requests.SkillRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.SkillResponse
import com.jobsearch.userservice.services.SkillService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class SkillController(
    private val skillService: SkillService
) {
    @QueryMapping
    fun userSkills(@AuthenticationPrincipal userId: UUID): List<SkillResponse> {
        return skillService.getUserSkills(userId)
    }

    @MutationMapping
    fun addSkill(@AuthenticationPrincipal userId: UUID,
                 @Argument @Valid skillRequest: SkillRequest): SkillResponse {
        return skillService.addSkill(userId, skillRequest)
    }

    @MutationMapping
    fun deleteSkillById(@AuthenticationPrincipal userId: UUID,
                 @Argument skillId: UUID): List<SkillResponse> {
        return skillService.deleteSkill(userId, skillId)
    }

    @MutationMapping
    fun deleteAllSkills(@AuthenticationPrincipal userId: UUID): DeleteResponse {
        return skillService.deleteAllSkills(userId)
    }

    @QueryMapping
    fun allSkills(): List<String> {
        return skillService.getAllSkills()
    }

    @QueryMapping
    fun allProficiencyLevels(): List<String> {
        return skillService.getAllProficiencyLevels()
    }
}