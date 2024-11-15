package com.jobsearch.userservice.requests

interface PasswordConfirmation {
    val password: String
    val confirmPassword: String
}