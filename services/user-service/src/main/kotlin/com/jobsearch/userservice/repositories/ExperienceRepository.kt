package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.Experience
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ExperienceRepository: JpaRepository<Experience, UUID> {
    fun findByUserProfile(userProfile: UserProfile): List<Experience>
}