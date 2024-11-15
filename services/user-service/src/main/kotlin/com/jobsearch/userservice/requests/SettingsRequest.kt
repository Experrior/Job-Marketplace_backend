package com.jobsearch.userservice.requests

import jakarta.validation.constraints.NotNull

data class SettingsRequest(
    @field:NotNull(message = "Offers notification must not be null")
    val offersNotification: Boolean,

    @field:NotNull(message = "Newsletter notification must not be null")
    val newsletterNotification: Boolean,

    @field:NotNull(message = "Recruiter messages must not be null")
    val recruiterMessages: Boolean,

    @field:NotNull(message = "Push notification must not be null")
    val pushNotification: Boolean
)