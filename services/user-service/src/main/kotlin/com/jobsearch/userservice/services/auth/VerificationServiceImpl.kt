package com.jobsearch.userservice.services.auth

import com.jobsearch.messagequeue.RabbitMQMessageProducer
import com.jobsearch.userservice.entities.EmailType
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.exceptions.InvalidTokenException
import com.jobsearch.userservice.exceptions.UserNotFoundException
import com.jobsearch.userservice.requests.EmailRequest
import com.jobsearch.userservice.services.UserService
import com.jobsearch.userservice.services.auth.tokens.VerificationTokenService
import org.springframework.stereotype.Service
import java.util.*

@Service
class VerificationServiceImpl(
    private val messageProducer: RabbitMQMessageProducer,
    private val verificationTokenService: VerificationTokenService,
    private val userService: UserService
    ): VerificationService {

    override fun sendVerificationEmail(user: User) {
        val verificationToken = verificationTokenService.generateVerificationToken(user)

        messageProducer.publish(
            generateEmailRequest(user.email, verificationToken.token),
            "internal.exchange",
            "internal.email.routing-key"
        )
    }

    override fun verifyUserByToken(token: String): User {
        val verificationToken = verificationTokenService.getVerificationToken(token)
        if (verificationToken.expiryDate.before(Date()))
            throw InvalidTokenException("Token has expired")

        val user = verificationToken.user ?: throw UserNotFoundException("User not found with token: $token")

        user.isEmailVerified = true
        userService.save(user)

        verificationTokenService.deleteVerificationToken(verificationToken)

        return user
    }

    private fun generateEmailRequest(to: String, token: String): EmailRequest {
        return EmailRequest(
            to = to,
            message = token,
            emailType = EmailType.VERIFICATION_EMAIL
        )
    }
}