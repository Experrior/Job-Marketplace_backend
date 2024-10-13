package com.jobsearch.userservice.services.auth

import com.jobsearch.userservice.config.KeycloakProvider
import com.jobsearch.userservice.exceptions.InvalidCredentialsException
import com.jobsearch.userservice.exceptions.UserNotVerifiedException
import com.jobsearch.userservice.requests.LoginRequest
import com.jobsearch.userservice.responses.TokenResponse
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotAuthorizedException
import org.keycloak.representations.AccessTokenResponse
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.naming.AuthenticationException

@Service
class LoginServiceImpl(
    private val kcProvider: KeycloakProvider
): LoginService {
    @Value("\${keycloak.realm}")
    lateinit var realm: String
    private val logger = LoggerFactory.getLogger(LoginServiceImpl::class.java)

    override fun login(loginRequest: LoginRequest): TokenResponse {
        return try {
            val keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(
                loginRequest.email, loginRequest.password
            ).build()

            val accessTokenResponse = keycloak.tokenManager().accessToken
            generateTokenResponse(accessTokenResponse)
        }
        catch (e: BadRequestException){
            logger.error(e.message)
            handleBadRequestException(loginRequest.email)
        }catch (e: NotAuthorizedException){
            logger.error(e.message)
            throw InvalidCredentialsException()
        }catch (e: Exception){
            logger.error(e.message)
            throw AuthenticationException("Invalid login credentials or authentication failed.")
        }
    }

    private fun generateTokenResponse(accessTokenResponse: AccessTokenResponse): TokenResponse{
        return TokenResponse(
            accessToken = accessTokenResponse.token,
            expiresIn = accessTokenResponse.expiresIn,
            refreshToken = accessTokenResponse.refreshToken,
            refreshExpiresIn = accessTokenResponse.refreshExpiresIn,
        )
    }

    private fun handleBadRequestException(email: String): Nothing {
        val kcUserId = fetchKcUserIdByEmail(email)
        val userRepresentation = fetchUserRepresentation(kcUserId)

        verifyUserEmail(userRepresentation)

        throw BadRequestException()
    }

    private fun fetchKcUserIdByEmail(email: String): String {
        val usersResource = kcProvider.getInstance().realm(realm).users()
        val userList = usersResource.search(email)

        if (userList.isEmpty()) {
            throw InvalidCredentialsException()
        }

        return userList.first().id
    }

    private fun fetchUserRepresentation(kcUserId: String): UserRepresentation {
        val usersResource = kcProvider.getInstance().realm(realm).users()
        return usersResource.get(kcUserId).toRepresentation()
    }

    private fun verifyUserEmail(userRepresentation: UserRepresentation) {
        if (!userRepresentation.isEmailVerified) {
            throw UserNotVerifiedException()
        }
    }
}