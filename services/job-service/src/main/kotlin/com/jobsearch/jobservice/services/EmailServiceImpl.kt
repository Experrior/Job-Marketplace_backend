package com.jobsearch.jobservice.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*
import java.io.FileNotFoundException

@Service
class EmailServiceImpl(
    private val sesClient: SesClient
) : EmailService {
    @Value("\${email.sender}")
    private lateinit var emailSender: String

    private val logger = LoggerFactory.getLogger(EmailServiceImpl::class.java)

    override fun sendEmail(to: String, subject: String, body: String) {
        val destination = Destination.builder()
            .toAddresses(to)
            .build()

        val subjectContent = Content.builder()
            .data(subject)
            .build()

        val htmlContent = Content.builder()
            .data(body)
            .build()

        val message = Message.builder()
            .subject(subjectContent)
            .body(Body.builder().html(htmlContent).build())
            .build()

        val sendEmailRequest = SendEmailRequest.builder()
            .destination(destination)
            .message(message)
            .source(emailSender)
            .build()

        try {
            sesClient.sendEmail(sendEmailRequest)
        } catch (ex: Exception) {
            logger.error("Error sending email to $to: ${ex.message}")
        }
    }


    override fun loadTemplate(templateName: String, variables: Map<String, String>): String {
        val classLoader = this::class.java.classLoader
        val templatePath = "templates/$templateName"
        val template = classLoader.getResource(templatePath)?.readText(Charsets.UTF_8)
            ?: throw FileNotFoundException("Template file not found: $templatePath")

        var populatedTemplate = template
        variables.forEach { (key, value) ->
            populatedTemplate = populatedTemplate.replace("\${$key}", value)
        }

        return populatedTemplate
    }

}
