package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.exceptions.UserAlreadyExistsException
import com.jobsearch.userservice.exceptions.UserRegistrationException
import com.jobsearch.userservice.requests.RegistrationRequest
import com.jobsearch.userservice.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegistrationServiceImpl(
    private val userService: UserService,
    private val verificationService: VerificationService,
    private val passwordEncoder: BCryptPasswordEncoder
): RegistrationService {
    private val logger = LoggerFactory.getLogger(RegistrationServiceImpl::class.java)

    override fun registerUser(registrationRequest: RegistrationRequest, userRole: UserRole): UUID {
        try {
            checkUserAlreadyExists(registrationRequest.email)
            val hashedPassword = passwordEncoder.encode(registrationRequest.password)
            val user = createUserEntity(registrationRequest, hashedPassword, userRole)
            val savedUser = userService.save(user)

            verificationService.sendVerificationEmail(savedUser)
            return savedUser.userId
        }catch (e: UserAlreadyExistsException){
            throw UserAlreadyExistsException()
        }catch (e: Exception) {
            logger.error("User registration failed: ${e.message}", e)
            throw UserRegistrationException("User registration failed: ${e.message}", 500)
        }
    }

    private fun createUserEntity(registrationRequest: RegistrationRequest, hashedPassword: String, userRole: UserRole): User {
        val companyId = userService.getCompanyIdByName(registrationRequest.company.orEmpty())

        return User(
            email = registrationRequest.email,
            password = hashedPassword,
            firstName = registrationRequest.firstName,
            lastName = registrationRequest.lastName,
            role = userRole,
            companyId = companyId,
            isEnabled = true,
            isEmailVerified = false,
            isEmployeeVerified = false
        )
    }

    private fun checkUserAlreadyExists(email: String){
        if (userService.existsByEmail(email)) {
            throw UserAlreadyExistsException()
        }
    }
}