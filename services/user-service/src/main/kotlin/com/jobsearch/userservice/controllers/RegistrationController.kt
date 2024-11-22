package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.requests.RegistrationRequest
import com.jobsearch.userservice.responses.RegistrationResponse
import com.jobsearch.userservice.services.auth.RegistrationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/register")
class RegistrationController(
    private val registrationService: RegistrationService
) {
    @PostMapping("/applicant")
    fun registerApplicant(@RequestBody @Valid registrationRequest: RegistrationRequest): ResponseEntity<RegistrationResponse> {
        val userId = registrationService.registerUser(registrationRequest, UserRole.APPLICANT)
        return createResponse("Applicant has been registered successfully. Verify your email!", userId)
    }

    @PostMapping("/recruiter")
    fun registerRecruiter(@RequestBody @Valid registrationRequest: RegistrationRequest): ResponseEntity<RegistrationResponse> {
        val userId = registrationService.registerUser(registrationRequest, UserRole.RECRUITER)
        return createResponse("Recruiter has been registered successfully. Verify your email!", userId)
    }

    @PostMapping("/company", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun registerCompany(
        @RequestParam("registrationRequest") registrationRequestString: String,
        @RequestParam("logo") logo: MultipartFile
    ): ResponseEntity<RegistrationResponse> {
        val companyId = registrationService.registerCompany(registrationRequestString, logo)
        return createResponse("Company has been registered successfully. Verify your email!", companyId)
    }


    private fun createResponse(message: String, id: UUID?): ResponseEntity<RegistrationResponse> {
        val response = RegistrationResponse(
            message = message,
            id = id!!
        )
        return ResponseEntity(response, HttpStatus.CREATED)
    }
}