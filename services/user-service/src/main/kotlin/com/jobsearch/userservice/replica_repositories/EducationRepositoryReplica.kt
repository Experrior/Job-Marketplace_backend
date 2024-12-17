package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.Education
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EducationRepositoryReplica: JpaRepository<Education, UUID> {
    fun findByUserProfile(userProfile: UserProfile): List<Education>
    fun deleteByEducationId(educationId: UUID): Boolean
}