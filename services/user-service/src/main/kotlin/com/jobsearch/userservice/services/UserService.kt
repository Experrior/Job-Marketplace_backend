package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.User
import org.keycloak.representations.idm.UserRepresentation
import java.util.*

interface UserService {
    fun getUserById(userId: UUID): User
    fun getAllUsers(limit: Int = 10, offset: Int = 0): List<User>
    fun getUserFromRepresentation(userRepresentation: UserRepresentation, keycloakUserId: String, companyId: UUID?): User
    fun save(user: User)
    fun saveAll(users: List<User>)
    fun getCompanyIdByName(companyName: String?): UUID?
    fun isUserEligibleForProfile(user: User): Boolean
    fun existsByUserId(userId: UUID): Boolean
}