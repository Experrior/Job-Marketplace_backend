package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.ProficiencyLevel
import com.jobsearch.userservice.entities.Skill
import com.jobsearch.userservice.entities.SkillType
import com.jobsearch.userservice.exceptions.SkillNotFoundException
import com.jobsearch.userservice.repositories.SkillRepository
import com.jobsearch.userservice.requests.SkillRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.SkillResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class SkillServiceImpl(
    private val skillRepository: SkillRepository,
    private val userProfileService: UserProfileService,
    private val mapper: UserProfileMapper
): SkillService {
    override fun getUserSkills(userId: UUID): List<SkillResponse> {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        return skillRepository.findByUserProfile(profile).map { mapper.toSkillResponse(it) }
    }

    @Transactional
    override fun addSkill(userId: UUID, skillRequest: SkillRequest): SkillResponse {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        val skillType = SkillType.valueOf(skillRequest.skillName.uppercase())

        val skill = Skill(
            userProfile = profile,
            skillName = skillType.displayName,
            proficiencyLevel = skillRequest.proficiencyLevel
        )

        return mapper.toSkillResponse(skillRepository.save(skill))
//        return skillRepository.findByUserProfile(profile).map { mapper.toSkillResponse(it) }
    }

    @Transactional
    override fun deleteSkill(userId: UUID, skillId: UUID): List<SkillResponse> {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        if(skillRepository.findBySkillIdAndUserProfile(skillId, profile) == null) {
            throw SkillNotFoundException(skillId.toString())
        }

        skillRepository.deleteById(skillId)
        return skillRepository.findByUserProfile(profile).map { mapper.toSkillResponse(it) }
    }

    @Transactional
    override fun deleteAllSkills(userId: UUID): DeleteResponse {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        val skills = skillRepository.findByUserProfile(profile)

        return try {
            skillRepository.deleteAll(skills)
            DeleteResponse(
                success = true,
                message = "All skills deleted successfully"
            )
        } catch (e: Exception) {
            DeleteResponse(
                success = false,
                message = e.message ?: "An error occurred while deleting skills"
            )
        }
    }

    override fun getAllSkills(): List<String> {
        return SkillType.entries.map { it.name }
    }

    override fun getAllProficiencyLevels(): List<String> {
        return ProficiencyLevel.entries.map { it.name }
    }
}