package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.Skill
import com.jobsearch.userservice.requests.SkillRequest
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
    fun userSkills(@AuthenticationPrincipal userId: String): List<Skill> {
        return skillService.getUserSkills(UUID.fromString(userId))
    }

    @MutationMapping
    fun addSkill(@AuthenticationPrincipal userId: String,
                 @Argument @Valid skillRequest: SkillRequest): Skill {
        return skillService.addSkill(UUID.fromString(userId), skillRequest)
    }

    @MutationMapping
    fun deleteSkillById(@AuthenticationPrincipal userId: String,
                 @Argument skillId: UUID): Boolean {
        return skillService.deleteSkill(UUID.fromString(userId), skillId)
    }

    @MutationMapping
    fun deleteAllSkills(@AuthenticationPrincipal userId: String): Boolean {
        return skillService.deleteAllSkills(UUID.fromString(userId))
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