package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.exceptions.CompanyAlreadyExistsException
import com.jobsearch.userservice.exceptions.CompanyNotVerifiedException
import com.jobsearch.userservice.exceptions.UserAlreadyExistsException
import com.jobsearch.userservice.exceptions.UserRegistrationException
import com.jobsearch.userservice.requests.CompanyRegistrationRequest
import com.jobsearch.userservice.requests.RegistrationRequest
import com.jobsearch.userservice.services.CompanyService
import com.jobsearch.userservice.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegistrationServiceImpl(
    private val userService: UserService,
    private val verificationService: VerificationService,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val companyService: CompanyService
): RegistrationService {
    private val logger = LoggerFactory.getLogger(RegistrationServiceImpl::class.java)

    override fun registerUser(registrationRequest: RegistrationRequest, userRole: UserRole): UUID? {
        try {
            checkUserAlreadyExists(registrationRequest.email)
            registrationRequest.company?.let { checkCompanyVerified(it) }

            val hashedPassword = passwordEncoder.encode(registrationRequest.password)
            val user = createUserEntity(registrationRequest, hashedPassword, userRole)
            val savedUser = userService.save(user)

            verificationService.sendVerificationEmail(savedUser)
            if (userRole == UserRole.RECRUITER) {
                val companyEmail = getCompanyEmail(savedUser.companyId!!)
                verificationService.sendEmployeeVerificationEmail(savedUser, companyEmail)
            }

            return savedUser.userId
        }catch (e: UserAlreadyExistsException){
            throw UserAlreadyExistsException()
        }catch (e: CompanyNotVerifiedException){
            throw e
        }catch (e: Exception) {
            logger.error("User registration failed: ${e.message}", e)
            throw UserRegistrationException("User registration failed: ${e.message}", 500)
        }
    }

    override fun registerCompany(registrationRequest: CompanyRegistrationRequest): UUID? {
        try {
            checkCompanyAlreadyExists(registrationRequest.email, registrationRequest.companyName)
            val company = createCompanyEntity(registrationRequest)
            val savedCompany = companyService.save(company)

            verificationService.sendVerificationEmail(savedCompany)
            return savedCompany.companyId
        }catch (e: CompanyAlreadyExistsException) {
            throw e.message?.let { CompanyAlreadyExistsException(it) }!!
        }catch (e: Exception) {
            logger.error("Company registration failed: ${e.message}", e)
            throw UserRegistrationException("Company registration failed: ${e.message}", 500)
        }
    }

    private fun createUserEntity(registrationRequest: RegistrationRequest, hashedPassword: String, userRole: UserRole): User {
        var companyId: UUID? = null
        if(registrationRequest.company != null)
            companyId = companyService.getCompanyIdByName(registrationRequest.company)

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

    private fun createCompanyEntity(registrationRequest: CompanyRegistrationRequest): Company {
        return Company(
            email = registrationRequest.email,
            name = registrationRequest.companyName,
            industry = registrationRequest.industry,
            description = registrationRequest.description ?: "",
            logoPath = registrationRequest.logo,
            isEmailVerified = false
        )
    }

    private fun checkUserAlreadyExists(email: String){
        if (userService.existsByEmail(email)) {
            throw UserAlreadyExistsException()
        }
    }

    private fun checkCompanyAlreadyExists(email: String, name: String){
        if (companyService.existsByEmail(email)) {
            throw CompanyAlreadyExistsException("Company with email $email already exists")
        }else if (companyService.existsByName(name)) {
            throw CompanyAlreadyExistsException("Company with name $name already exists")
        }
    }

    private fun getCompanyEmail(companyId: UUID): String {
        return companyService.findCompanyById(companyId).email
    }

    private fun checkCompanyVerified(companyName: String) {
        val companyId = companyService.getCompanyIdByName(companyName)
        if (!companyService.findCompanyById(companyId).isEmailVerified) {
            throw CompanyNotVerifiedException("Company is not verified")
        }
    }
}
