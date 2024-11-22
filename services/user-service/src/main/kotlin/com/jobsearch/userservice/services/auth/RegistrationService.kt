package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.requests.CompanyRegistrationRequest
import com.jobsearch.userservice.requests.RegistrationRequest
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface RegistrationService {
    fun registerUser(registrationRequest: RegistrationRequest, userRole: UserRole): UUID?
    fun registerCompany(registrationRequest: CompanyRegistrationRequest, logo: MultipartFile): UUID?
}