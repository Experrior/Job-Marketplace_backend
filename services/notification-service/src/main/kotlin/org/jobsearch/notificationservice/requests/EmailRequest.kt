package org.jobsearch.notificationservice.requests

data class EmailRequest (
    val emailType: EmailType,
    val to: String,
    val message: String
)