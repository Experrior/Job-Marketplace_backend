package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface UserRepositoryReplica: JpaRepository<User, UUID>{
    fun existsByEmail(email: String): Boolean
    fun findUserDetailsByEmail(email: String): UserDetails?
    fun findByEmail(email: String): User?
}