package com.jobsearch.userservice.requests

import com.jobsearch.userservice.validators.ValidProficiencyLevel
import com.jobsearch.userservice.validators.ValidSkill

data class SkillRequest(
    @field:ValidSkill
    val skillName: String,

    @field:ValidProficiencyLevel
    val proficiencyLevel: String
)
