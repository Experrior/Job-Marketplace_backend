package com.jobsearch.userservice.services.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.exceptions.CompanyAlreadyExistsException
import com.jobsearch.userservice.exceptions.CompanyNotVerifiedException
import com.jobsearch.userservice.exceptions.UserAlreadyExistsException
import com.jobsearch.userservice.requests.CompanyRegistrationRequest
import com.jobsearch.userservice.requests.RegistrationRequest
import com.jobsearch.userservice.services.CompanyService
import com.jobsearch.userservice.services.FileStorageService
import com.jobsearch.userservice.services.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class RegistrationServiceImpl(
    private val userService: UserService,
    private val verificationService: VerificationService,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val companyService: CompanyService,
    private val fileStorageService: FileStorageService,
    private val objectMapper: ObjectMapper
) : RegistrationService {

    override fun registerUser(registrationRequest: RegistrationRequest, userRole: UserRole): UUID {
        checkUserAlreadyExists(registrationRequest.email)
        validateRegistrationRequest(registrationRequest, userRole)

        val hashedPassword = passwordEncoder.encode(registrationRequest.password)
        val user = createUserEntity(registrationRequest, hashedPassword, userRole)
        val savedUser = userService.save(user)

        sendVerificationEmails(savedUser, userRole)

        return savedUser.userId!!
    }

    override fun registerCompany(registrationRequestString: String, logo: MultipartFile): UUID {
        val registrationRequest = objectMapper.readValue(registrationRequestString, CompanyRegistrationRequest::class.java)

        checkCompanyAlreadyExists(registrationRequest.email, registrationRequest.companyName)

        val company = createCompanyEntity(registrationRequest, logo)
        verificationService.sendVerificationEmail(company)

        return company.companyId!!
    }

    private fun checkUserAlreadyExists(email: String) {
        if (userService.existsByEmail(email)) {
            throw UserAlreadyExistsException("User with email $email already exists")
        }
    }

    private fun checkCompanyAlreadyExists(email: String, name: String) {
        when {
            companyService.existsByEmail(email) -> throw CompanyAlreadyExistsException("Company with email $email already exists")
            companyService.existsByName(name) -> throw CompanyAlreadyExistsException("Company with name $name already exists")
        }
    }

    private fun validateRegistrationRequest(registrationRequest: RegistrationRequest, userRole: UserRole) {
        if (userRole == UserRole.RECRUITER && registrationRequest.company.isNullOrBlank()) {
            throw IllegalArgumentException("Company is required for recruiters")
        }
        registrationRequest.company?.let { checkCompanyVerified(it) }
    }

    private fun checkCompanyVerified(companyName: String) {
        val companyId = companyService.findCompanyIdByName(companyName)
        val company = companyService.findCompanyById(companyId)

        if (!company.isEmailVerified) {
            throw CompanyNotVerifiedException("Company $companyName is not verified")
        }
    }

    private fun createUserEntity(registrationRequest: RegistrationRequest, hashedPassword: String, userRole: UserRole): User {
        val companyId = registrationRequest.company?.let { companyService.findCompanyIdByName(it) }
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

    private fun createCompanyEntity(registrationRequest: CompanyRegistrationRequest, logo: MultipartFile): Company {
        val company = Company(
            email = registrationRequest.email,
            name = registrationRequest.companyName,
            industry = registrationRequest.industry,
            description = registrationRequest.description ?: "",
            isEmailVerified = false
        )

        val savedCompany = companyService.save(company)
        val logoPath = fileStorageService.storeCompanyLogo(savedCompany.companyId!!, logo)
        savedCompany.s3LogoPath = logoPath

        return companyService.save(savedCompany)
    }

    private fun sendVerificationEmails(user: User, userRole: UserRole) {
        verificationService.sendVerificationEmail(user)
        if (userRole == UserRole.RECRUITER) {
            val companyEmail = getCompanyEmail(user.companyId!!)
            verificationService.sendEmployeeVerificationEmail(user, companyEmail)
        }
    }

    private fun getCompanyEmail(companyId: UUID): String {
        return companyService.findCompanyById(companyId).email
    }
}
