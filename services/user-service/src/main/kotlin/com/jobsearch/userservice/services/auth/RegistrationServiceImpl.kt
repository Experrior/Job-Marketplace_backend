package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.config.KeycloakProvider
import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.exceptions.UserRegistrationException
import com.jobsearch.userservice.requests.RegistrationRequest
import com.jobsearch.userservice.services.UserService
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import org.json.JSONObject
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.util.*
import java.util.stream.Collectors

@Service
class RegistrationServiceImpl(
    private val userService: UserService,
    private val kcProvider: KeycloakProvider
): RegistrationService {
    private val logger = LoggerFactory.getLogger(RegistrationServiceImpl::class.java)

    @Value("\${keycloak.realm}")
    lateinit var realm: String
    @Value("\${keycloak.resource}")
    lateinit var clientId: String

    override fun registerUser(registrationRequest: RegistrationRequest, userRole: UserRole): UUID {
        try {
            val usersResource = kcProvider.getInstance().realm(realm).users()
            val kcUserRepresentation = createKeycloakUser(registrationRequest)
            val response = usersResource.create(kcUserRepresentation)

            return if (response.status == 201) {
                handleSuccessfulRegistration(response, kcUserRepresentation, registrationRequest, userRole)
            } else {
                throw UserRegistrationException(extractErrorMessage(response), response.status)
            }
        } catch (e: WebApplicationException) {
            throw UserRegistrationException(extractErrorMessage(e.response), e.response.status)
        }
    }

    private fun handleSuccessfulRegistration(
        response: Response,
        kcUserRepresentation: UserRepresentation,
        registrationRequest: RegistrationRequest,
        userRole: UserRole
    ): UUID {
        val keycloakUserId = extractUserId(response)
        val usersResource = kcProvider.getInstance().realm(realm).users()
        val companyId = userService.getCompanyIdByName(registrationRequest.company.orEmpty())

        // Assign role and proceed with the user creation flow
        assignClientRole(keycloakUserId, usersResource, userRole)

        val user = userService.getUserFromRepresentation(kcUserRepresentation, keycloakUserId, companyId)
        userService.save(user)

//        sendVerificationEmail(user.keycloakUserId)

        return user.userId ?: throw IllegalStateException("User ID is null")
    }

    private fun extractErrorMessage(response: Response): String {
        return try {
            val responseBody = response.readEntity(String::class.java)
            JSONObject(responseBody).optString("errorMessage", "An unknown error occurred")
        } catch (ex: Exception) {
            "Error while processing the response"
        }
    }

    fun createKeycloakUser(user: RegistrationRequest): UserRepresentation {
        val credentialRepresentation = createPasswordCredentials(user.password)
        val kcUser = UserRepresentation().apply {
            email = user.email
            credentials = Collections.singletonList(credentialRepresentation)
            firstName = user.firstName
            lastName = user.lastName
            isEnabled = true
            isEmailVerified = false
            requiredActions = listOf("VERIFY_EMAIL")
        }

        return kcUser
    }

    fun sendVerificationEmail(userId: String) {
        val usersResource = kcProvider.getInstance().realm(realm).users()
        usersResource[userId].sendVerifyEmail()
    }

    private fun extractUserId(response: Response): String {
        val location: URI = response.location
        val path: String = location.path
        return path.substring(path.lastIndexOf('/') + 1)
    }

    private fun assignClientRole(userId: String, usersResource: UsersResource, userRole: UserRole): String? {
        val clientUuid = kcProvider.getInstance().realm(realm).clients().findByClientId(clientId)[0].id

        val roleMappingResource = usersResource[userId].roles()
        val clientRolesResource = roleMappingResource.clientLevel(clientUuid)

        // Get existing client roles
        val availableRoles = clientRolesResource.listAvailable()

        val rolesToAdd = availableRoles.stream()
            .filter { role: RoleRepresentation -> role.name == userRole.roleName}
            .collect(Collectors.toList())

        clientRolesResource.add(rolesToAdd)
        return rolesToAdd
            .stream()
            .findFirst()
            .map { obj: RoleRepresentation -> obj.name }
            .orElse(null)
    }

    private fun createPasswordCredentials(password: String): CredentialRepresentation {
        val passwordCredentials = CredentialRepresentation()
        passwordCredentials.isTemporary = false
        passwordCredentials.type = CredentialRepresentation.PASSWORD
        passwordCredentials.value = password
        return passwordCredentials
    }

}