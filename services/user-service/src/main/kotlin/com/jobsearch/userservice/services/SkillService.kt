package com.jobsearch.userservice.services

import com.jobsearch.userservice.requests.SkillRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.SkillResponse
import java.util.*

interface SkillService {
    fun getUserSkills(userId: UUID): List<SkillResponse>
    fun addSkill(userId: UUID, skillRequest: SkillRequest): List<SkillResponse>
    fun deleteSkill(userId: UUID, skillId: UUID): List<SkillResponse>
    fun deleteAllSkills(userId: UUID): DeleteResponse
    fun getAllSkills(): List<String>
    fun getAllProficiencyLevels(): List<String>
}