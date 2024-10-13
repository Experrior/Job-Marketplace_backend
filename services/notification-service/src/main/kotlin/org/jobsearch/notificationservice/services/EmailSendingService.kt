package org.jobsearch.notificationservice.services

import org.jobsearch.notificationservice.requests.EmailRequest

interface EmailSendingService {
    fun sendVerificationEmail(emailRequest: EmailRequest)
    fun sendResetPasswordEmail(emailRequest: EmailRequest)
}