package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.services.auth.KeycloakUserService
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Service
class UserSyncService(
    val userService: UserService,
    val keycloakUserService: KeycloakUserService
): CommandLineRunner {

    fun syncUsers(users: List<UserRepresentation>) {
        val newUsers = users.filter { keycloakUser ->
            !userService.existsByUserId(UUID.fromString(keycloakUser.id))
        }.map { keycloakUser ->
            val role = keycloakUserService.fetchUserClientRole(keycloakUser.id)

            User(
                userId = UUID.fromString(keycloakUser.id),
                email = keycloakUser.email ?: "",
                firstName = keycloakUser.firstName ?: "",
                lastName = keycloakUser.lastName ?: "",
                role = role,
                isEnabled = keycloakUser.isEnabled,
                isEmailVerified = keycloakUser.isEmailVerified,
                createdAt = keycloakUser.createdTimestamp?.let { Timestamp(it) } ?: Timestamp.from(Instant.now()),
                updatedAt = Timestamp.from(Instant.now())
            )
        }

        if (newUsers.isNotEmpty()) {
            userService.saveAll(newUsers)
        }
    }

    override fun run(vararg args: String?) {
        val keycloakUsers = keycloakUserService.fetchAllUsersFromKeycloak()
        syncUsers(keycloakUsers)
    }
}