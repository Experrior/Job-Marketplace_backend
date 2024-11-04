package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.requests.CompanyRegistrationRequest
import com.jobsearch.userservice.requests.RegistrationRequest
import com.jobsearch.userservice.services.auth.RegistrationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/register")
class RegistrationController(
    private val registrationService: RegistrationService
) {
    @PostMapping("/applicant")
    fun registerApplicant(@RequestBody @Valid registrationRequest: RegistrationRequest): ResponseEntity<String>{
        registrationService.registerUser(registrationRequest, UserRole.APPLICANT)

        return ResponseEntity<String>(
            "Applicant has been registered successfully. Verify your email!",
            HttpStatus.CREATED
        )
    }

    @PostMapping("/recruiter")
    fun registerRecruiter(@RequestBody @Valid registrationRequest: RegistrationRequest): ResponseEntity<String>{
        if (registrationRequest.company.isNullOrBlank()) {
            return ResponseEntity(
                "Company is required for recruiters",
                HttpStatus.BAD_REQUEST
            )
        }

        registrationService.registerUser(registrationRequest, UserRole.RECRUITER)

        return ResponseEntity<String>(
            "Recruiter has been registered successfully. Verify your email!",
            HttpStatus.CREATED
        )
    }

    @PostMapping("/company")
    fun registerCompany(@RequestBody @Valid registrationRequest: CompanyRegistrationRequest): ResponseEntity<String>{
        registrationService.registerCompany(registrationRequest)

        return ResponseEntity<String>(
            "Company has been registered successfully. Verify your email!",
            HttpStatus.CREATED
        )
    }
}