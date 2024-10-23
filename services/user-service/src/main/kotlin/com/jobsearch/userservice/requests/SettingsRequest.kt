package com.jobsearch.userservice.requests

data class SettingsRequest(
    val offersNotification: Boolean,
    val newsletterNotification: Boolean,
    val recruiterMessages: Boolean,
    val pushNotification: Boolean
)