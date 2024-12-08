package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.Experience
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ExperienceRepositoryReplica: JpaRepository<Experience, UUID> {
    fun findByUserProfile(userProfile: UserProfile): List<Experience>
}