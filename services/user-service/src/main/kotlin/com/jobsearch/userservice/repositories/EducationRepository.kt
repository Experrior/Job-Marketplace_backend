package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.Education
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EducationRepository: JpaRepository<Education, UUID> {
    fun findByUserProfile(userProfile: UserProfile): List<Education>
    fun deleteByEducationId(educationId: UUID): Boolean
}