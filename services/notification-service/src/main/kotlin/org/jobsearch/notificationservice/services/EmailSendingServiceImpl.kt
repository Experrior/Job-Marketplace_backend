package org.jobsearch.notificationservice.services

import jakarta.mail.MessagingException
import org.jobsearch.notificationservice.requests.EmailRequest
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
        val verificationUrl = "http://$hostName/user-service/verify-email?token=${emailRequest.message}"
        sendEmail(emailRequest.to, "Email Verification", "verification-email.html", "verificationLink", verificationUrl)
    }

    override fun sendResetPasswordEmail(emailRequest: EmailRequest) {
        val resetPasswordUrl = "http://$hostName/user-service/updatePassword?token=${emailRequest.message}"
        sendEmail(emailRequest.to, "Password Reset", "reset-password-email.html", "resetPasswordLink", resetPasswordUrl)
    }

    private fun sendEmail(to: String, subject: String, templateName: String, placeholder: String, link: String) {
        val mail = mailSender.createMimeMessage()
        try {
            val helper = MimeMessageHelper(mail, true)
            helper.setTo(to)
            helper.setSubject(subject)
            var content = loadTemplate(templateName)
            content = content.replace("\${$placeholder}", link)
            helper.setText(content, true)
            mailSender.send(mail)
        } catch (e: MessagingException) {
            throw RuntimeException("Failed to send email", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to send email", e)
        }
    }


    @Throws(IOException::class)
    private fun loadTemplate(filename: String): String {
        val resource = resourceLoader.getResource("classpath:templates/$filename")
        resource.inputStream.use { inputStream ->
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8)
        }
    }
}