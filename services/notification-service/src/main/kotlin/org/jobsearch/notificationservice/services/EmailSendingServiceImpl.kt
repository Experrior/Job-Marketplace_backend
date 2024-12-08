package org.jobsearch.notificationservice.services

import jakarta.mail.MessagingException
import org.jobsearch.notificationservice.requests.EmailRequest
import org.jobsearch.notificationservice.requests.EmailType
import org.jobsearch.notificationservice.requests.EmployeeDetails
import org.jobsearch.notificationservice.requests.Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import java.io.IOException
import java.nio.charset.StandardCharsets

@Service
class EmailSendingServiceImpl(
    private val mailSender: JavaMailSender,
    private val resourceLoader: ResourceLoader
): EmailSendingService {
    @Value("\${gateway.hostname}")
    lateinit var hostName: String

    override fun sendVerificationEmail(emailRequest: EmailRequest) {
        val verificationUrl = buildUrl("/user-service/verification/verify-email", emailRequest.message)
        val placeholders = mapOf(
            "verificationLink" to verificationUrl
        )
        sendEmail(emailRequest.to, "Email Verification", Template.VERIFICATION_EMAIL, placeholders)
    }

    override fun sendResetPasswordEmail(emailRequest: EmailRequest) {
        val resetPasswordUrl = buildUrl("/reset-password", emailRequest.message)
        val placeholders = mapOf(
            "resetPasswordLink" to resetPasswordUrl
        )
        sendEmail(emailRequest.to, "Password Reset", Template.RESET_PASSWORD_EMAIL, placeholders)
    }

    override fun sendEmployeeVerificationEmail(emailRequest: EmailRequest) {
        val approveLink = buildUrl("/user-service/verification/approve-employee", emailRequest.message)
        val rejectLink = buildUrl("/user-service/verification/reject-employee", emailRequest.message)
        val placeholders = mapOf(
            "approveLink" to approveLink,
            "rejectLink" to rejectLink
        )
        sendEmail(emailRequest.to, "Employee Verification", Template.EMPLOYEE_VERIFICATION_EMAIL, placeholders, emailRequest.employeeDetails)
    }

    override fun sendEmployeeVerificationConfirmationEmail(emailRequest: EmailRequest) {
        val (templateName, subject) = when (emailRequest.emailType) {
            EmailType.EMPLOYEE_VERIFICATION_APPROVED ->
                Template.VERIFICATION_APPROVED to "Employee Verification Approved"
            EmailType.EMPLOYEE_VERIFICATION_REJECTED ->
                Template.VERIFICATION_REJECTED to "Employee Verification Rejected"
            else -> throw IllegalArgumentException("Unsupported email type: ${emailRequest.emailType}")
        }

        sendEmail(emailRequest.to, subject, templateName, emptyMap())
    }

    private fun sendEmail(to: String, subject: String, template: Template, placeholders: Map<String, String> = emptyMap(), employeeDetails: EmployeeDetails? = null) {
        val mail = mailSender.createMimeMessage()
        try {
            val helper = MimeMessageHelper(mail, true)
            helper.setTo(to)
            helper.setSubject(subject)
            var content = loadTemplate(template.fileName)
            content = replacePlaceholders(content, placeholders, employeeDetails)
            helper.setText(content, true)
            mailSender.send(mail)
        } catch (e: MessagingException) {
            throw RuntimeException("Failed to send email", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to send email", e)
        }
    }

    private fun replacePlaceholders(content: String, placeholders: Map<String, String>, employeeDetails: EmployeeDetails?): String {
        var updatedContent = content
        for ((placeholder, link) in placeholders) {
            updatedContent = updatedContent.replace("\${$placeholder}", link)
        }
        employeeDetails?.let {
            updatedContent = updatedContent
                .replace("[firstName]", it.firstName)
                .replace("[lastName]", it.lastName)
                .replace("[employeeEmail]", it.email)
        }
        return updatedContent
    }

    @Throws(IOException::class)
    private fun loadTemplate(filename: String): String {
        val resource = resourceLoader.getResource("classpath:templates/$filename")
        resource.inputStream.use { inputStream ->
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8)
        }
    }

    private fun buildUrl(endpoint: String, token: String): String {
        return "$hostName$endpoint?token=$token"
    }

}