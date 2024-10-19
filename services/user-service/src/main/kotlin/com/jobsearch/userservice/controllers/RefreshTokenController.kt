package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.responses.TokenResponse
import com.jobsearch.userservice.services.auth.tokens.RefreshTokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/token")
class RefreshTokenController(
    private val refreshTokenService: RefreshTokenService
) {
    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestParam("refreshToken") refreshToken: String): ResponseEntity<TokenResponse> {
        return ResponseEntity(
            refreshTokenService.refreshAccessToken(refreshToken),
            HttpStatus.OK
        )
    }
}