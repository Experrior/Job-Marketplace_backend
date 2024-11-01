package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.User
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface UserService {
    fun getUserById(userId: UUID): User
    fun getAllUsers(limit: Int = 10, offset: Int = 0): List<User>
    fun save(user: User): User
    fun saveAll(users: List<User>)
    fun isUserEligibleForProfile(user: User): Boolean
    fun existsByUserId(userId: UUID): Boolean
    fun existsByEmail(email: String): Boolean
    fun loadUserByUsername(username: String): UserDetails
    fun getUserByEmail(email: String): User
}