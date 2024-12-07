package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
    fun existsByUser(user: User): Boolean
    fun findByUser(user: User): UserProfile?
}