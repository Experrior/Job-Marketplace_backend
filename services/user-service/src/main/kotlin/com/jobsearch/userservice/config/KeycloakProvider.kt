package com.jobsearch.userservice.config

import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration


@Configuration
class KeycloakProvider {

    @Value("\${keycloak.auth-server-url}")
    lateinit var serverURL: String

    @Value("\${keycloak.realm}")
    lateinit var realm: String

    @Value("\${keycloak.resource}")
    lateinit var clientID: String

    @Value("\${keycloak.credentials.secret}")
    lateinit var clientSecret: String

    companion object {
        private var keycloak: Keycloak? = null
    }

    fun getInstance(): Keycloak {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                .realm(realm)
                .serverUrl(serverURL)
                .clientId(clientID)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build()
        }
        return keycloak!!
    }

    fun newKeycloakBuilderWithPasswordCredentials(username: String, password: String): KeycloakBuilder {
        return KeycloakBuilder.builder()
            .realm(realm)
            .serverUrl(serverURL)
            .clientId(clientID)
            .clientSecret(clientSecret)
            .username(username)
            .password(password)
    }
}
