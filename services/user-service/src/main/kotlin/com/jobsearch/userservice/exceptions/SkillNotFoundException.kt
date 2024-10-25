package com.jobsearch.userservice.exceptions

class SkillNotFoundException(skill: String) : RuntimeException("Skill $skill not found") {
}