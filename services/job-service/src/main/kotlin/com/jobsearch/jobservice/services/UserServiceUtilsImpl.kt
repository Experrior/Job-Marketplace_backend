package com.jobsearch.jobservice.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class UserServiceUtilsImpl(
    webClientBuilder: WebClient.Builder
) : UserServiceUtils {

    private val webClient: WebClient = webClientBuilder.build()

    @Value("\${user.service.url}")
    lateinit var userServiceUrl: String

    private fun createHeaders(): Map<String, String> {
        val authentication = SecurityContextHolder.getContext().authentication
        return mapOf(
            "X-User-Id" to authentication.name,
            "X-User-Roles" to authentication.authorities.joinToString(",") { it.authority }
        )
    }

    private fun executeGraphQLQuery(query: String): Map<String, Any?>? {
        val headers = createHeaders()
        return webClient.post()
            .uri("$userServiceUrl/graphql")
            .headers { it.setAll(headers) }
            .bodyValue(mapOf("query" to query))
            .retrieve()
            .bodyToMono(Any::class.java)
            .block()?.let {
                @Suppress("UNCHECKED_CAST")
                it as? Map<String, Any?>
            }
    }

    override fun getRecruiterCompany(): UUID {
        val query = """
            query {
                recruiterCompany
            }
        """.trimIndent()

        val response = executeGraphQLQuery(query)
        val companyId = (response?.get("data") as? Map<*, *>)?.get("recruiterCompany") as? String
            ?: throw IllegalStateException("Recruiter company ID not found")

        return UUID.fromString(companyId)
    }

    override fun getApplicantFullName(userId: UUID): String {
        val query = """
            query {
                userFullName(userId: "$userId")
            }
        """.trimIndent()

        val response = executeGraphQLQuery(query)
        return (response?.get("data") as? Map<*, *>)?.get("userFullName") as? String
            ?: throw IllegalStateException("User full name not found")
    }
}
