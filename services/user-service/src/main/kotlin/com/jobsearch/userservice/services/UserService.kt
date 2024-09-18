package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.User
import java.util.UUID

interface UserService {
    fun getUserById(userId: UUID): User?
    fun getAllUsers(limit: Int = 10, offset: Int = 0): List<User>
    fun registerUser()
}