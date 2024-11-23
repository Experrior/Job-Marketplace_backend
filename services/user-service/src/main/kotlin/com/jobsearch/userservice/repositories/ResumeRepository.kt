package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.Resume
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ResumeRepository: JpaRepository<Resume, UUID> {
    fun findByUserProfile(userProfile: UserProfile): List<Resume>
}