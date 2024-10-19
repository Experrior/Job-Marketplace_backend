package com.jobsearch.userservice.services.auth.tokens

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.entities.VerificationToken
import com.jobsearch.userservice.exceptions.InvalidTokenException
import com.jobsearch.userservice.repositories.VerificationTokenRepository
import org.springframework.stereotype.Service

@Service
class VerificationTokenServiceImpl(
    private val verificationTokenRepository: VerificationTokenRepository
): VerificationTokenService {
    override fun generateVerificationToken(user: User): VerificationToken {
        val verificationToken = VerificationToken(user = user)

        return verificationTokenRepository.save(verificationToken)
    }

    override fun getVerificationToken(token: String): VerificationToken {
        return verificationTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Token not found: $token")
    }

    override fun deleteVerificationToken(token: VerificationToken) {
        verificationTokenRepository.delete(token)
    }
}