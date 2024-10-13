package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.exceptions.CompanyNotFoundException
import com.jobsearch.userservice.exceptions.UserNotFoundException
import com.jobsearch.userservice.repositories.UserRepository
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
): UserService{
    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    @Value("\${keycloak.realm}")
    lateinit var realm: String

    override fun getUserById(userId: UUID): User {
        return userRepository.findById(userId)
            .orElseThrow {UserNotFoundException("User with ID $userId not found")}
    }

    override fun getAllUsers(limit: Int, offset: Int): List<User> {
        return userRepository.findAll();
    }

    override fun getUserFromRepresentation(userRepresentation: UserRepresentation, keycloakUserId: String, companyId: UUID?): User {
        return User(
            userId = UUID.randomUUID(),
            keycloakUserId = keycloakUserId,
            email = userRepresentation.email,
            firstName = userRepresentation.firstName,
            lastName = userRepresentation.lastName,
            role = getUserRole(userRepresentation),
            companyId = companyId,
            isEnabled = userRepresentation.isEnabled,
            isEmailVerified = userRepresentation.isEmailVerified
        )
    }

    override fun save(user: User) {
        userRepository.save(user)
    }

    override fun saveAll(users: List<User>) {
        userRepository.saveAll(users)
    }

    override fun getCompanyIdByName(companyName: String?): UUID? {
        return if (companyName.isNullOrBlank()) {
            null
        } else {
            try {
                Company.valueOf(companyName).companyId
            }catch (e: IllegalArgumentException){
                throw CompanyNotFoundException(companyName)
            }
        }
    }

    override fun findByKeycloakUserId(keycloakUserId: String): User {
        return userRepository.findByKeycloakUserId(keycloakUserId)
            ?: throw UserNotFoundException("User with Keycloak ID $keycloakUserId not found")
    }

    override fun existsByKeycloakUserId(keycloakUserId: String): Boolean {
        return userRepository.existsByKeycloakUserId(keycloakUserId)
    }

    override fun isUserEligibleForProfile(user: User): Boolean {
        return user.isEmailVerified && user.isEnabled
    }

    override fun getUserIdFromAuthentication(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        val kcUserId = (authentication.principal as Jwt).subject
        logger.info("User ID extracted: $kcUserId")
        return getUserIdByKeycloakUserId(kcUserId)
    }

    override fun getUserIdByKeycloakUserId(keycloakUserId: String): UUID {
        return userRepository.findUserIdByKeycloakUserId(keycloakUserId)
            ?: throw UserNotFoundException("User with Keycloak ID $keycloakUserId not found")    }

    private fun getUserRole(userRepresentation: UserRepresentation): UserRole{
        val clientRoles: Map<String, List<String>>? = userRepresentation.clientRoles
        val jobsearchRoles: List<String>? = clientRoles?.get("jobsearch")
        val userRole: String = jobsearchRoles?.firstOrNull() ?: "APPLICANT"
        return UserRole.valueOf(userRole)
    }




}