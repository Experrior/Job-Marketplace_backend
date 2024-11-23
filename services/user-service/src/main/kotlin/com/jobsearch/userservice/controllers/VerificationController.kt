package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.responses.ApiResponse
import com.jobsearch.userservice.services.auth.VerificationService
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
    @PostMapping("/verify-email")
    fun verifyEmail(@RequestParam("token") token: String): ResponseEntity<ApiResponse> {
        verificationService.verifyByToken(token)

        return ResponseEntity(
            ApiResponse(message = "Email verified"),
            HttpStatus.OK
        )
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
