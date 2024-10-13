package org.jobsearch.notificationservice.rabbitmq

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component


@Component

class EmailConsumer {
    private val emailService: EmailService? = null

    @RabbitListener(queues = ["\${rabbitmq.queue.email}"])
    fun consumer(emailRequest: EmailRequest) {
        try {
            log.info(("Sending message of " + emailRequest.getEmailType()).toString() + " type")
            when (emailRequest.getEmailType()) {
                VERIFICATION_EMAIL -> emailService.sendVerificationMail(emailRequest)
                RESET_PASSWORD_EMAIL -> emailService.sendResetPasswordMail(emailRequest)
                BUY_ORDER -> emailService.sendBuyOrderMail(emailRequest)
                SELL_ORDER -> emailService.sendSellOrderMail(emailRequest)
                else -> log.warn("Unhandled email type: {}", emailRequest.getEmailType())
            }
        } catch (e: Exception) {
            log.error("Error processing message", e)
        }
    }
}