package com.jobsearch.jobservice.config

import jakarta.ws.rs.HttpMethod
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val customHeaderAuthenticationFilter: CustomHeaderAuthenticationFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/jobs/create", hasRole("RECRUITER"))
                authorize(HttpMethod.POST,"/jobs/{jobId}", hasRole("RECRUITER"))
                authorize(HttpMethod.DELETE,"/jobs/{jobId}", hasRole("RECRUITER"))
                authorize(HttpMethod.PUT,"/jobs/{jobId}", hasRole("RECRUITER"))
                authorize("/applications/{jobId}", hasRole("RECRUITER"))
                authorize("/applications/{applicationId}/apply", hasRole("APPLICANT"))
                authorize("/applications", hasRole("APPLICANT"))
                authorize("/applications/{applicationId}/status", hasRole("RECRUITER"))
                authorize(anyRequest, authenticated)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            exceptionHandling {
                authenticationEntryPoint = unauthorizedEntryPoint()
            }
            anonymous { disable() }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(customHeaderAuthenticationFilter)
        }
        return http.build()
    }

    @Bean
    fun unauthorizedEntryPoint(): AuthenticationEntryPoint {
        return HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}