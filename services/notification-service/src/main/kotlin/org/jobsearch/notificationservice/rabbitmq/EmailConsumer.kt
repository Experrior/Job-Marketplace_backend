package org.jobsearch.notificationservice.rabbitmq

import org.jobsearch.notificationservice.requests.EmailRequest
import org.jobsearch.notificationservice.requests.EmailType
import org.jobsearch.notificationservice.services.EmailSendingService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component


@Component
class EmailConsumer (
    private val emailSendingService: EmailSendingService
){
    private val logger = LoggerFactory.getLogger(EmailConsumer::class.java)

    @RabbitListener(queues = ["\${rabbitmq.queue.email}"])
    fun consumer(emailRequest: EmailRequest) {
        try {
            logger.info(("Sending message of " + emailRequest.emailType) + " type")
            when (emailRequest.emailType) {
                EmailType.VERIFICATION_EMAIL -> emailSendingService.sendVerificationEmail(emailRequest)
                EmailType.RESET_PASSWORD_EMAIL -> emailSendingService.sendResetPasswordEmail(emailRequest)
            }
        } catch (e: Exception) {
            logger.error("Error processing message", e)
        }
    }
}