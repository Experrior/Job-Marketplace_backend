package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.requests.ResetPasswordRequest
import com.jobsearch.userservice.requests.UpdatePasswordRequest
import com.jobsearch.userservice.responses.ApiResponse
import com.jobsearch.userservice.services.auth.tokens.ResetPasswordService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/password")
class ResetPasswordController(
    private val resetPasswordService: ResetPasswordService
) {
    @PostMapping("/reset")
    fun resetPassword(@RequestBody @Valid resetPasswordRequest: ResetPasswordRequest): ResponseEntity<ApiResponse>{
        resetPasswordService.resetPassword(resetPasswordRequest.email)

        return ResponseEntity(
            ApiResponse(message = "Reset password link has been sent"),
            HttpStatus.OK
        )
    }

    @GetMapping("/validateToken")
    fun validateResetToken(@RequestParam("token") token: String): ResponseEntity<ApiResponse> {
        resetPasswordService.validateToken(token)

        return ResponseEntity(
            ApiResponse(message = "Token is valid"),
            HttpStatus.OK
        )
    }

    @PostMapping("/update")
    fun updatePassword(
        @RequestParam("token") resetPasswordToken: String,
        @RequestBody @Valid updatePasswordRequest: UpdatePasswordRequest): ResponseEntity<ApiResponse>{
        resetPasswordService.updatePassword(resetPasswordToken, updatePasswordRequest)

        return ResponseEntity(
            ApiResponse(message = "Password has been updated"),
            HttpStatus.OK
        )
    }
}