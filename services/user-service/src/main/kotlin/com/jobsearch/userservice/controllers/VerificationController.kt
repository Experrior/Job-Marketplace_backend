package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.services.auth.VerificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class VerificationController(
    private val verificationService: VerificationService
) {
    @GetMapping("/verify-email")
    fun verifyEmail(@RequestParam("token") token: String
    ): ResponseEntity<String> {
        verificationService.verifyByToken(token)

        return ResponseEntity<String>("Email verified", HttpStatus.OK)
    }

}