package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.requests.LoginRequest
import com.jobsearch.userservice.responses.TokenResponse
import com.jobsearch.userservice.services.auth.LoginService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/login")
class LoginController(
    private val loginService: LoginService
) {
    @PostMapping
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse>{
        return ResponseEntity<TokenResponse>(
            loginService.login(loginRequest),
            HttpStatus.OK
        )
    }

}