package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.requests.RegistrationRequest
import java.util.*

interface RegistrationService {
    fun registerUser(registrationRequest: RegistrationRequest, userRole: UserRole): UUID
}