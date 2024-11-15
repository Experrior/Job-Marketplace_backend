package com.jobsearch.userservice.config

import com.jobsearch.userservice.entities.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private var secretKeyString: String,
    @Value("\${jwt.expiration-s}")
    private val validityInSeconds: Long,
    @Value("\${jwt.refresh-expiration-s}")
    private val refreshValidityInSeconds: Long,
) {
    private lateinit var secretKey: SecretKey

    @PostConstruct
    fun init() {
        secretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())
    }

    fun generateToken(authentication: Authentication): String {
        val user = authentication.principal as User

        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["sub"] = user.userId.toString()
        claims["email"] = user.email
        claims["given_name"] = user.firstName
        claims["family_name"] = user.lastName
        claims["email_verified"] = user.isEmailVerified
        claims["roles"] = user.authorities.map { it.authority }
        claims["token_type"] = "access_token"

        val now = Date()
        val validity = Date(now.time + validityInSeconds * 1000)

        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(authentication: Authentication): String {
        val user = authentication.principal as User

        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["sub"] = user.userId.toString()
        claims["email"] = user.email
        claims["token_type"] = "refresh_token"

        val now = Date()
        val validity = Date(now.time + refreshValidityInSeconds * 1000)

        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey)
            .compact()
    }

    fun getEmailFromToken(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload


        return claims["email"] as String
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isTokenExpired(token: String): Boolean {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.expiration.before(Date())
    }

    fun getValidityInMilliseconds(): Long {
        return validityInSeconds
    }

    fun getRefreshValidityInMilliseconds(): Long {
        return refreshValidityInSeconds
    }

}