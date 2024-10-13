package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.config.KeycloakProvider
import com.jobsearch.userservice.entities.UserRole
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KeycloakUserServiceImpl(
    private val kcProvider: KeycloakProvider
): KeycloakUserService {
    @Value("\${keycloak.realm}")
    lateinit var realm: String
    @Value("\${keycloak.resource}")
    lateinit var clientId: String

    override fun fetchAllUsersFromKeycloak(): List<UserRepresentation> {
        val keycloak: Keycloak = kcProvider.getInstance()
        val usersResource = keycloak.realm(realm).users()

        val users = mutableListOf<UserRepresentation>()
        var firstResult = 0
        val maxResults = 50

        do {
            val paginatedUsers = usersResource.list(firstResult, maxResults)
            users.addAll(paginatedUsers)
            firstResult += paginatedUsers.size
        } while (paginatedUsers.size == maxResults)

        return users
    }

    override fun fetchUserClientRole(userId: String): UserRole {
        val keycloak = kcProvider.getInstance()
        val clientUuid = keycloak.realm(realm)
            .clients()
            .findByClientId(clientId)[0].id

        val usersResource = keycloak.realm(realm).users()
        val roleMappingResource = usersResource.get(userId).roles()
        val clientRoles = roleMappingResource.clientLevel(clientUuid).listAll()

        return mapKeycloakRoleToUserRole(clientRoles)
    }

    private fun mapKeycloakRoleToUserRole(clientRoles: List<RoleRepresentation>): UserRole {
        return when {
            clientRoles.any { it.name == "ROLE_APPLICANT" } -> UserRole.APPLICANT
            clientRoles.any { it.name == "ROLE_RECRUITER" } -> UserRole.RECRUITER
            clientRoles.any { it.name == "ROLE_ADMIN" } -> UserRole.ADMIN
            else -> UserRole.APPLICANT
        }
    }
}