package com.jobsearch.userservice.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class TestController(
    ) {
    @GetMapping("/public")
    fun public(): String{
        return "Welcome to public"
    }

    @GetMapping("/protected")
    fun protected(): String{
        return "Welcome to protected"
    }

    @GetMapping("/applicant")
    fun applicant(): String{
        return "Welcome to applicant's page"
    }

    @GetMapping("/recruiter")
    fun recruiter(): String{
        return "Welcome to recruiter's page"
    }

}