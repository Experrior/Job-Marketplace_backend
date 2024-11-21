package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.ProficiencyLevel
import com.jobsearch.userservice.entities.Skill
import com.jobsearch.userservice.entities.SkillType
import com.jobsearch.userservice.exceptions.SkillNotFoundException
import com.jobsearch.userservice.repositories.SkillRepository
import com.jobsearch.userservice.requests.SkillRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class SkillServiceImpl(
    private val skillRepository: SkillRepository,
    private val userProfileService: UserProfileService
): SkillService {
    override fun getUserSkills(userId: UUID): List<Skill> {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        return skillRepository.findByUserProfile(profile)
    }

    override fun addSkill(userId: UUID, skillRequest: SkillRequest): Skill {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        val proficiencyLevel = ProficiencyLevel.valueOf(skillRequest.proficiencyLevel)

        val skill = Skill(
            userProfile = profile,
            skillName = skillRequest.skillName,
            proficiencyLevel = proficiencyLevel
        )

        return skillRepository.save(skill)
    }

    override fun deleteSkill(userId: UUID, skillId: UUID): Boolean {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        if(skillRepository.findBySkillIdAndUserProfile(skillId, profile) == null) {
            throw SkillNotFoundException(skillId.toString())
        }

        return try {
            skillRepository.deleteById(skillId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun deleteAllSkills(userId: UUID): Boolean {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        val skills = skillRepository.findByUserProfile(profile)

        return try {
            skillRepository.deleteAll(skills)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getAllSkills(): List<String> {
        return SkillType.entries.map { it.name }
    }

    override fun getAllProficiencyLevels(): List<String> {
        return ProficiencyLevel.entries.map { it.name }
    }
}