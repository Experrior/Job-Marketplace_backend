package com.jobsearch.userservice.responses

import java.util.*

data class SkillResponse(
    val skillId: UUID,
    val skillName: String,
    val proficiencyLevel: Int
)
