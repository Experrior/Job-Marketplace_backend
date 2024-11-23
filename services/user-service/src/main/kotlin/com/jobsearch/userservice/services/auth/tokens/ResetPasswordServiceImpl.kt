package com.jobsearch.userservice.services.auth.tokens

import com.jobsearch.messagequeue.RabbitMQMessageProducer
import com.jobsearch.userservice.entities.ResetPasswordToken
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.exceptions.InvalidTokenException
import com.jobsearch.userservice.exceptions.UserNotFoundException
import com.jobsearch.userservice.repositories.ResetPasswordTokenRepository
import com.jobsearch.userservice.requests.UpdatePasswordRequest
import com.jobsearch.userservice.services.UserService
import jakarta.transaction.Transactional
import org.jobsearch.notificationservice.requests.EmailRequest
import org.jobsearch.notificationservice.requests.EmailType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class ResetPasswordServiceImpl(
    private val userService: UserService,
    private val resetPasswordTokenRepository: ResetPasswordTokenRepository,
    private val messageProducer: RabbitMQMessageProducer,
    private val passwordEncoder: BCryptPasswordEncoder
): ResetPasswordService {
    companion object {
        const val TOKEN_EXPIRATION_TIME = 15 * 60 * 1000 // 15 minutes
    }

    override fun resetPassword(email: String) {
        val user = userService.getUserByEmail(email)

        sendResetPasswordEmail(user)
    }

    @Transactional
    override fun updatePassword(token: String, updatePasswordRequest: UpdatePasswordRequest) {
        val user = validateResetToken(token)
        updateUserPassword(user, updatePasswordRequest.password)
        resetPasswordTokenRepository.deleteByToken(token)
    }

    override fun validateToken(token: String) {
        val resetToken = resetPasswordTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("The reset password token is invalid.")

        if (resetToken.isExpired()) {
            throw InvalidTokenException("The reset password token has expired.")
        }

        val user = resetToken.user ?: throw InvalidTokenException("The user associated with this token does not exist.")

        if (!userService.existsByUserId(user.userId!!)) {
            throw InvalidTokenException("The user associated with this token does not exist.")
        }
    }

    fun generateResetToken(user: User): String {
        val token = UUID.randomUUID().toString()
        val expiryDate = Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME)
        val resetToken = resetPasswordTokenRepository.findByUser(user)?.apply {
            this.token = token
            this.expiryDate = expiryDate
        } ?: ResetPasswordToken(token = token, user = user, expiryDate = expiryDate)

        resetPasswordTokenRepository.save(resetToken)
        return token
    }

    private fun sendResetPasswordEmail(user: User) {
        val resetToken = generateResetToken(user)

        messageProducer.publish(
            generateEmailRequest(user.email, resetToken),
            "internal.exchange",
            "internal.email.routing-key"
        )
    }

    private fun generateEmailRequest(to: String, token: String): EmailRequest {
        return EmailRequest(
            to = to,
            message = token,
            emailType = EmailType.RESET_PASSWORD
        )
    }

    private fun validateResetToken(token: String): User {
        val resetToken = resetPasswordTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid or expired password reset token")
        if (resetToken.expiryDate.before(Date())) {
            throw InvalidTokenException("Password reset token has expired")
        }
        return resetToken.user ?: throw UserNotFoundException("User not found with token: $token")
    }

    private fun updateUserPassword(user: User, newPassword: String) {
        user.password = passwordEncoder.encode(newPassword)
        userService.save(user)
    }
}