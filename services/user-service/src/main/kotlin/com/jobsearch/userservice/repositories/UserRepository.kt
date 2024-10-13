package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository : JpaRepository<User, UUID>{
    fun findByKeycloakUserId(keycloakUserId: String): User?
    @Query("SELECT u.userId FROM users u WHERE u.keycloakUserId = :keycloakUserId")
    fun findUserIdByKeycloakUserId(keycloakUserId: String): UUID?
    fun existsByKeycloakUserId(keycloakUserId: String): Boolean
}