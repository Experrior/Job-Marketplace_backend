package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.entities.VerificationResult
import com.jobsearch.userservice.responses.ApiResponse
import com.jobsearch.userservice.services.auth.VerificationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/verification")
class VerificationController(
    private val verificationService: VerificationService
) {
    @Value("\${frontend.url}")
    private lateinit var frontendUrl: String

    @PostMapping("/verify-email")
    fun verifyEmail(@RequestParam("token") token: String): ResponseEntity<Void> {
        val redirectUrl = when (val result = verificationService.verifyByToken(token)) {
            is VerificationResult.UserVerified -> {
                if (result.user.role == UserRole.RECRUITER && !result.user.isEmployeeVerified) {
                    "${frontendUrl}/login?emailVerified=true&awaitingApproval=true"
                } else {
                    "${frontendUrl}/login?emailVerified=true"
                }
            }
            is VerificationResult.CompanyVerified -> "${frontendUrl}/login?companyVerified=true"
        }

        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", redirectUrl)
            .build()
    }


    @PostMapping("/approve-employee")
    fun approveEmployee(@RequestParam("token") token: String): ResponseEntity<ApiResponse> {
        verificationService.approveEmployee(token)

        return ResponseEntity(
            ApiResponse(message = "Employee approved"),
            HttpStatus.OK
        )
    }

    @PostMapping("/reject-employee")
    fun rejectEmployee(@RequestParam("token") token: String): ResponseEntity<ApiResponse> {
        verificationService.rejectEmployee(token)

        return ResponseEntity(
            ApiResponse(message = "Employee rejected"),
            HttpStatus.OK
        )
    }
}
