package com.jobsearch.userservice.services.auth.tokens

import com.jobsearch.userservice.config.JwtTokenProvider
import com.jobsearch.userservice.exceptions.InvalidTokenException
import com.jobsearch.userservice.responses.TokenResponse
import com.jobsearch.userservice.services.UserService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class RefreshTokenServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
): RefreshTokenService {
    override fun refreshAccessToken(refreshToken: String): TokenResponse {
        validateToken(refreshToken)

        val email = jwtTokenProvider.getEmailFromToken(refreshToken)
        val user = userService.getUserByEmail(email)

        val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)

        return generateTokenResponse(authentication)
    }

    private fun validateToken(refreshToken: String){
        if (!jwtTokenProvider.validateToken(refreshToken) ||
            jwtTokenProvider.isTokenExpired(refreshToken)) {
            throw InvalidTokenException("Refresh token is invalid or expired")
        }
    }

    private fun generateTokenResponse(authentication: Authentication): TokenResponse{
        val newAccessToken = jwtTokenProvider.generateToken(authentication)
        val newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication)

        return TokenResponse(
            accessToken = newAccessToken,
            expiresIn = jwtTokenProvider.getValidityInMilliseconds(),
            refreshToken = newRefreshToken,
            refreshExpiresIn = jwtTokenProvider.getRefreshValidityInMilliseconds()
        )
    }
}