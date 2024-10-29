package com.jobsearch.jobservice.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CustomHeaderAuthenticationFilter : OncePerRequestFilter() {
    private val logger1 = LoggerFactory.getLogger(CustomHeaderAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val userId = request.getHeader("X-User-Id")
        val roles = request.getHeader("X-User-Roles")?.split(",")?.map { SimpleGrantedAuthority(it) } ?: emptyList()
        logger1.info("Roles: $roles")
        logger1.info("userId: $userId")
        if (userId != null) {
            val authentication = UsernamePasswordAuthenticationToken(userId, null, roles)
            SecurityContextHolder.getContext().authentication = authentication
            logger1.info("Security context populated with user: ${SecurityContextHolder.getContext().authentication.authorities}")
        }

        filterChain.doFilter(request, response)
    }
}