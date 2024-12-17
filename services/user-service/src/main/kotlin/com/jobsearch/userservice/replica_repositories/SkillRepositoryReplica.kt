package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.Skill
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SkillRepositoryReplica: JpaRepository<Skill, UUID> {
    fun findByUserProfile(userProfile: UserProfile): List<Skill>
    fun findBySkillIdAndUserProfile(skillId: UUID, userProfile: UserProfile): Skill?
}