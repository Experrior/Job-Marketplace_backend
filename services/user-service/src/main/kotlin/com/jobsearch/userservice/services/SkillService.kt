package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Skill
import com.jobsearch.userservice.requests.SkillRequest
import java.util.*

interface SkillService {
    fun getUserSkills(userId: UUID): List<Skill>
    fun addSkill(userId: UUID, skillRequest: SkillRequest): Skill
    fun deleteSkill(userId: UUID, skillId: UUID): Boolean
    fun deleteAllSkills(userId: UUID): Boolean
    fun getAllSkills(): List<String>
    fun getAllProficiencyLevels(): List<String>
}