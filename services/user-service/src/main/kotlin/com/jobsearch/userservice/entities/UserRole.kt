package com.jobsearch.userservice.entities

enum class UserRole(val roleName: String) {
    APPLICANT("ROLE_APPLICANT"),
    RECRUITER("ROLE_RECRUITER"),
    ADMIN("ROLE_ADMIN")
}