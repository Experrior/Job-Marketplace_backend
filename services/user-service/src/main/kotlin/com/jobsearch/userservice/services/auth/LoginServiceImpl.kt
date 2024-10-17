package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.config.JwtTokenProvider
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.exceptions.InvalidCredentialsException
import com.jobsearch.userservice.exceptions.UserNotVerifiedException
import com.jobsearch.userservice.requests.LoginRequest
import com.jobsearch.userservice.responses.TokenResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.naming.AuthenticationException

@Service
class LoginServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
): LoginService {
    private val logger = LoggerFactory.getLogger(LoginServiceImpl::class.java)

    override fun login(loginRequest: LoginRequest): TokenResponse {
        return try {
            val authentication = authenticateUser(loginRequest)
            setSecurityContext(authentication)

            generateTokenResponse(authentication)
        }catch (e: UserNotVerifiedException){
            throw UserNotVerifiedException()
        }catch (e: UsernameNotFoundException){
            throw InvalidCredentialsException()
        }catch (e: InvalidCredentialsException){
            throw InvalidCredentialsException()
        }catch (e: Exception){
            logger.error(e.message)
            throw AuthenticationException("Invalid login credentials or authentication failed.")
        }
    }

    private fun authenticateUser(loginRequest: LoginRequest): Authentication {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )

            val user = authentication.principal as User
            verifyUserEmail(user)

            return authentication
        }catch (e: BadCredentialsException){
            throw InvalidCredentialsException()
        }
    }

    private fun setSecurityContext(authentication: Authentication) {
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun generateTokenResponse(authentication: Authentication): TokenResponse {
        val token = jwtTokenProvider.generateToken(authentication)
        val refreshToken = jwtTokenProvider.generateRefreshToken(authentication)

        return TokenResponse(
            accessToken = token,
            expiresIn = jwtTokenProvider.getValidityInMilliseconds(),
            refreshToken = refreshToken,
            refreshExpiresIn = jwtTokenProvider.getRefreshValidityInMilliseconds()
        )
    }


    private fun verifyUserEmail(user: User) {
        if (!user.isEmailVerified) {
            throw UserNotVerifiedException()
        }
    }
}