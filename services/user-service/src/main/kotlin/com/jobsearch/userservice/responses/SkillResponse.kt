package com.jobsearch.userservice.responses

import com.jobsearch.userservice.entities.ProficiencyLevel
import java.util.*

data class SkillResponse(
    val skillId: UUID,
    val skillName: String,
    val proficiencyLevel: ProficiencyLevel
)
