package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.requests.ResetPasswordRequest
import com.jobsearch.userservice.requests.UpdatePasswordRequest
import com.jobsearch.userservice.responses.ApiResponse
import com.jobsearch.userservice.services.auth.tokens.ResetPasswordService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ResetPasswordController(
    private val resetPasswordService: ResetPasswordService
) {
    @PostMapping("/resetPassword")
    fun resetPassword(@RequestBody @Valid resetPasswordRequest: ResetPasswordRequest): ResponseEntity<ApiResponse>{
        resetPasswordService.resetPassword(resetPasswordRequest.email)

        return ResponseEntity(
            ApiResponse(message = "Reset password link has been sent"),
            HttpStatus.OK
        )
    }

    @PostMapping("/updatePassword")
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