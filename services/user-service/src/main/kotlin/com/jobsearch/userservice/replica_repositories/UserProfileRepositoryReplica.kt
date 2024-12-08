package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserProfileRepositoryReplica: JpaRepository<UserProfile, UUID> {
    fun existsByUser(user: User): Boolean
    fun findByUser(user: User): UserProfile?
}