package com.jobsearch.userservice.requests

import com.jobsearch.userservice.entities.EmailType

data class EmailRequest(
    val to: String,
    val message: String,
    val emailType: EmailType
)
