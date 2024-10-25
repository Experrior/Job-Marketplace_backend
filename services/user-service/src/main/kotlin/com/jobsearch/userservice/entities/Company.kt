package com.jobsearch.userservice.entities

import java.util.*

// For now
enum class Company(val companyId: UUID) {
    GOOGLE(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")),
    VISA(UUID.fromString("123e4567-e89b-12d3-a456-426614174001")),
    MICROSOFT(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
}

