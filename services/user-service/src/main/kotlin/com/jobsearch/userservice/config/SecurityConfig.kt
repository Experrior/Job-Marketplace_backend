package com.jobsearch.userservice.config

import com.jobsearch.userservice.entities.UserRole
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val jwtConverter: JwtConverter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/register/**", permitAll)
                authorize("/login", permitAll)
                authorize("/recruiter", hasRole(UserRole.RECRUITER.name))
                authorize("/applicant", hasRole(UserRole.APPLICANT.name))
//                authorize("/graphql", authenticated)
//                authorize("/protected", authenticated)
                authorize(anyRequest, authenticated)

            }
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = jwtConverter
                }
            }
        }
        return http.build()
    }
}
//                authorize("/register/applicant", permitAll)
//                authorize("/public", permitAll)
//                authorize("/callback", permitAll)
//                authorize("/protected", authenticated)