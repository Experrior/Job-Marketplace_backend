package com.jobsearch.jobservice.services

interface EmailService {
    fun sendEmail(to: String, subject: String, body: String)
    fun loadTemplate(templateName: String, variables: Map<String, String>): String
}