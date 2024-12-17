package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.UserLink
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LinkRepositoryReplica: JpaRepository<UserLink, UUID> {
    fun findByUserProfile(userProfile: UserProfile): List<UserLink>
}