package com.jobsearch.userservice.requests

import com.jobsearch.userservice.validators.ValidSkill
import org.hibernate.validator.constraints.Range

data class SkillRequest(
    @field:ValidSkill
    val skillName: String,

    @field:Range(min = 1, max = 5)
    val proficiencyLevel: Int
)
