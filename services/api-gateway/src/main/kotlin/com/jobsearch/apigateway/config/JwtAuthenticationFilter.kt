package com.jobsearch.apigateway.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider) : WebFilter {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = extractToken(exchange)
        if (token == null) {
            logger.error("TOKEN NOT FOUND")
            return chain.filter(exchange)
        }

        return if (jwtTokenProvider.validateToken(token)) {
            processToken(exchange, chain, token)
        } else {
            logger.error("TOKEN IS MALFORMED OR EXPIRED")
            onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED)
        }
    }

    private fun extractToken(exchange: ServerWebExchange): String? {
        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        return if (!authHeader.isNullOrEmpty() && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            null
        }
    }

    private fun processToken(exchange: ServerWebExchange, chain: WebFilterChain, token: String): Mono<Void> {
        val roles = jwtTokenProvider.getRoles(token)
        val userId = jwtTokenProvider.getUserIdFromToken(token)

        val modifiedRequest = exchange.request.mutate()
            .header("X-User-Id", userId)
            .header("X-User-Roles", roles.joinToString(","))
            .build()

        val modifiedExchange = exchange.mutate().request(modifiedRequest).build()

        return chain.filter(modifiedExchange)
    }

    private fun onError(exchange: ServerWebExchange, err: String, httpStatus: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = httpStatus
        response.headers.add("error-message", err)
        return response.setComplete()
    }
}
