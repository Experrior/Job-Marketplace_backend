package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.UserRole
import org.keycloak.representations.idm.UserRepresentation

interface KeycloakUserService {
    fun fetchAllUsersFromKeycloak(): List<UserRepresentation>
    fun fetchUserClientRole(userId: String): UserRole
}