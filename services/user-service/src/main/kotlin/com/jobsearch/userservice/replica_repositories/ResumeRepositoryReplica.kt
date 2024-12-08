package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.Resume
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ResumeRepositoryReplica: JpaRepository<Resume, UUID> {
    fun findByUserProfile(userProfile: UserProfile): List<Resume>
}